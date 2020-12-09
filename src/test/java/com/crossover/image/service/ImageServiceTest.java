package com.crossover.image.service;

import com.crossover.image.db.entity.ImageEntity;
import com.crossover.image.db.respository.ImageRepository;
import com.crossover.image.exceptions.InvalidFileExtensionException;
import com.crossover.image.exceptions.InvalidFileFormatException;
import com.crossover.image.exceptions.InvalidFileSizeException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class ImageServiceTest {

    @MockBean
    private ImageRepository imageRepository;

    @InjectMocks
    ImageService imageService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = InvalidFileFormatException.class)
    public void uploadImageTestForNullFileFormat() throws InvalidFileSizeException, InvalidFileExtensionException, InvalidFileFormatException, IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);

        when(file.getContentType()).thenReturn(null);

        ImageService imageService = new ImageService();
        imageService.uploadImage(file);
    }

    @Test(expected = InvalidFileFormatException.class)
    public void uploadImageTestForInvalidFileFormat() throws InvalidFileSizeException, InvalidFileExtensionException, InvalidFileFormatException, IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);

        when(file.getContentType()).thenReturn("text/");

        ImageService imageService = new ImageService();
        imageService.uploadImage(file);
    }

    @Test(expected = InvalidFileExtensionException.class)
    public void uploadImageForInvalidFileExtension() throws InvalidFileSizeException, InvalidFileExtensionException, InvalidFileFormatException, IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);

        when(file.getContentType()).thenReturn("image/");
        when(file.getOriginalFilename()).thenReturn("filename.ico");

        ImageService imageService = new ImageService();
        imageService.uploadImage(file);
    }

    @Test(expected = InvalidFileSizeException.class)
    public void uploadImageForInvalidFileSize() throws IOException, InvalidFileSizeException, InvalidFileFormatException, InvalidFileExtensionException {
        MultipartFile file = Mockito.mock(MultipartFile.class);

        when(file.getBytes()).thenReturn(new byte[100]);
        when(file.getContentType()).thenReturn("image/");
        when(file.getOriginalFilename()).thenReturn("filename.png");
        when(file.getSize()).thenReturn(500L);

        imageService.setMaxFileSize(10);
        imageService.uploadImage(file);
    }

    @Test
    public void uploadImageTest() throws IOException, InvalidFileSizeException, InvalidFileFormatException, InvalidFileExtensionException {
        MultipartFile file = Mockito.mock(MultipartFile.class);

        when(file.getBytes()).thenReturn(new byte[500]);
        when(file.getContentType()).thenReturn("image/");
        when(file.getOriginalFilename()).thenReturn("filename.png");
        when(file.getSize()).thenReturn(500L);

        imageService.setMaxFileSize(100000);
        imageService.setAcessKey("AKIAJL34FCTYC44OZHOA");
        imageService.setSecretKey("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");
        imageService.setClientRegion("ap-south-1");
        imageService.setBucketName("crossover-image-bucket");

        String resultExpected = "https://s3." + "ap-south-1" + ".amazonaws.com/" + "crossover-image-bucket" + "/" + "filename.png";
        Assert.assertEquals(resultExpected, imageService.uploadImage(file));
    }

    @Test
    public void saveImageTest() {
        ImageEntity image = new ImageEntity();
        image.setSize(10);
        image.setDescription("description");
        image.setFileType("png");
        image.setPrimaryId(10);

        when(imageRepository.save(image)).thenReturn(image);

        Assert.assertEquals(image, imageService.saveImage(image));
    }
}
