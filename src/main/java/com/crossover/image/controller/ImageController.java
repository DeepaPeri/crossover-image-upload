package com.crossover.image.controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.crossover.image.db.entity.ImageEntity;
import com.crossover.image.exceptions.InvalidFileExtensionException;
import com.crossover.image.exceptions.InvalidFileFormatException;
import com.crossover.image.exceptions.InvalidFileSizeException;
import com.crossover.image.exceptions.RequestParamEmptyException;
import com.crossover.image.service.ImageService;
import java.io.IOException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping(value = "/api/images")
    @Produces({MediaType.APPLICATION_JSON})
    @ExceptionHandler(MultipartException.class)
    public Response uploadImage(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam(value = "description", required = false) String description) {

        String message = "";
        String imageUrl = "";
        HttpStatus status;

        try {
            if (description == null || file == null) {
                throw new RequestParamEmptyException("description or file cannot be null");
            }
            imageUrl = imageService.uploadImage(file);
            message = "Image successfully uploaded.";
            status = HttpStatus.CREATED;

            ImageEntity image = new ImageEntity();
            image.setDescription(description);
            image.setFileType(FilenameUtils.getExtension(file.getOriginalFilename()));
            image.setSize(file.getSize());

            imageService.saveImage(image);
        } catch (IOException e) {
            message = "Something wrong with the uploaded file.";
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
        } catch (AmazonServiceException e) {
            message = "Something wrong with Amazon S3.";
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
        } catch (SdkClientException e) {
            message = "Cannot connect with Amazon S3.";
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
        } catch (InvalidFileFormatException | InvalidFileExtensionException | InvalidFileSizeException | RequestParamEmptyException | MultipartException e) {
            message = e.getMessage();
            status = HttpStatus.BAD_REQUEST;
            e.printStackTrace();
        }
        throw new ResponseStatusException(status, message + ", image url: " + imageUrl);
    }
}
