package com.crossover.image.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.crossover.image.db.entity.ImageEntity;
import com.crossover.image.db.respository.ImageRepository;
import com.crossover.image.exceptions.InvalidFileExtensionException;
import com.crossover.image.exceptions.InvalidFileFormatException;
import com.crossover.image.exceptions.InvalidFileSizeException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Value("${crossover.aws.accesskey:AKIAJL34FCTYC44OZHOA}")
    private String acessKey;

    @Value("${crossover.aws.secretkey:ISy7AsuJTbQ/oUikFemMUx/YGgjJlvnaUBPrDg1S}")
    private String secretKey;

    @Value("${crossover.aws.s3.clientregion:ap-south-1}")
    private String clientRegion;

    @Value("${crossover.aws.s3.bucketname:crossover-image-bucket}")
    private String bucketName;

    @Value("${crossover.image.max-file-size:500000}")
    private Integer maxFileSize;

    public String uploadImage(MultipartFile file) throws IOException, InvalidFileFormatException, InvalidFileSizeException, InvalidFileExtensionException {
        byte[] bytes = file.getBytes();
        String fileObjKeyName = file.getOriginalFilename();
        String fileType = FilenameUtils.getExtension(file.getOriginalFilename());

        if (file.getContentType() == null) {
            throw new InvalidFileFormatException("File content type cannot be null");
        } else if (!file.getContentType().contains("image/")) {
            throw new InvalidFileFormatException("Only image format is accepted");
        }
        if (!"png".equals(fileType) && !"jpeg".equals(fileType) && !"jpg".equals(fileType)) {
            throw new InvalidFileExtensionException("Only jpeg and png files are accepted");
        } else if (file.getSize() > maxFileSize) {
            throw new InvalidFileSizeException("File size is allowed upto :" + maxFileSize + ". Please decrease the input file size and try agains");
        } else {
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(acessKey, secretKey);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(clientRegion)).withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            InputStream inputStream = new ByteArrayInputStream(bytes);
            PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, inputStream, metadata);

            s3Client.putObject(request);

            return "https://s3." + clientRegion + ".amazonaws.com/" + bucketName + "/" + fileObjKeyName;
        }
    }

    public ImageEntity saveImage(ImageEntity image) {
        return imageRepository.save(image);
    }

    public void setAcessKey(String acessKey) {
        this.acessKey = acessKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setClientRegion(String clientRegion) {
        this.clientRegion = clientRegion;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public void setMaxFileSize(Integer maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
}
