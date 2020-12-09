package com.crossover.image.controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.crossover.image.db.entity.ImageEntity;
import com.crossover.image.service.ImageService;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class ImageControllerTest {

    @MockBean
    private ImageService imageService;

    @InjectMocks
    ImageController imageController;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = ResponseStatusException.class)
    public void uploadImageForContentTypeNullTest(){
        MultipartFile file = mock(MultipartFile.class);
        String description = "description";

        when(file.getContentType()).thenReturn(null);
        imageController.uploadImage(file,description);
    }

    @Test(expected = ResponseStatusException.class)
    public void uploadImageForInvalidFileFormatTest(){
        MultipartFile file = mock(MultipartFile.class);
        String description = "description";

        when(file.getContentType()).thenReturn("text/");
        imageController.uploadImage(file,description);
    }

    @Test(expected = ResponseStatusException.class)
    public void uploadImageForInvalidFileExtensionTest(){
        MultipartFile file = mock(MultipartFile.class);
        String description = "description";

        when(file.getContentType()).thenReturn("image/");
        when(file.getOriginalFilename()).thenReturn("filename.ico");
        imageController.uploadImage(file,description);
    }

    @Test(expected = ResponseStatusException.class)
    public void uploadImageForInvalidFileSizeTest() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String description = "description";

        when(file.getBytes()).thenReturn(new byte[100]);
        when(file.getContentType()).thenReturn("image/");
        when(file.getOriginalFilename()).thenReturn("filename.png");
        when(file.getSize()).thenReturn(500L);

        imageService.setMaxFileSize(10);
        imageController.uploadImage(file,description);
    }

    @Test(expected = ResponseStatusException.class)
    public void uploadImageTest() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String description = "description";

        when(file.getBytes()).thenReturn(new byte[100]);
        when(file.getContentType()).thenReturn("image/");
        when(file.getOriginalFilename()).thenReturn("filename.png");
        when(file.getSize()).thenReturn(500L);

        imageService.setMaxFileSize(10);
        imageController.uploadImage(file,description);
    }

    @Test(expected = ResponseStatusException.class)
    public void uploadImageRequestParamNullTest() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String description = null;

        when(file.getBytes()).thenReturn(new byte[100]);
        when(file.getContentType()).thenReturn("image/");
        when(file.getOriginalFilename()).thenReturn("filename.png");
        when(file.getSize()).thenReturn(500L);

        imageService.setMaxFileSize(10);
        imageController.uploadImage(file,description);
    }

    @Test(expected = ResponseStatusException.class)
    public void uploadImageForAmazonServiceExceptionTest() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String description = "description";

        when(imageService.saveImage(any(ImageEntity.class))).thenThrow(AmazonServiceException.class);
        imageController.uploadImage(file,description);
    }

    @Test(expected = ResponseStatusException.class)
    public void uploadImageForSdkClientExceptionTest() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String description = "description";

        when(imageService.saveImage(any(ImageEntity.class))).thenThrow(SdkClientException.class);
        imageController.uploadImage(file,description);
    }

}
