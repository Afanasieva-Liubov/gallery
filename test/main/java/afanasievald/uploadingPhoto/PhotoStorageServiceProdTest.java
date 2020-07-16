package afanasievald.uploadingPhoto;

import afanasievald.databaseEntity.Folder;
import afanasievald.databaseEntity.Photo;
import afanasievald.repository.FolderRepository;
import afanasievald.repository.PhotoRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

@SpringJUnitConfig
@DataJpaTest
@ComponentScan("afanasievald.uploadingPhoto")
@ActiveProfiles("testprod")
class PhotoStorageServiceProdTest {

    @Autowired
    private StorageProperties storageProperties;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private S3Service s3Service;

    @NotNull
    AmazonS3 s3Client;

    @NotNull
    String bucketName;

    Random random = new Random();

    @BeforeEach
    void setUp() {
        this.s3Client = s3Service.getClient();
        this.bucketName = storageProperties.getBucketName();
    }

    @Test
    void testUploadPhotos_NullableMimeType() {
        Photo photo = storageService.uploadPhotos("testNotImageFile", "testNotImageFile".getBytes());
        assertNull(photo);
    }

    @Test
    void testUploadPhotos_NotImageMimeType() throws IOException {
        String photoLocation = storageProperties.getPhotoLocation();
        Path fileNameAndPath = Paths.get(photoLocation, "testWithMimeType.html");
        byte[] byteArray = Files.readAllBytes(fileNameAndPath);
        Photo photo = storageService.uploadPhotos("testWithMimeType.html", byteArray);
        assertNull(photo);
    }

    @Test
    void testUploadPhotos_ImageFile() throws IOException {
        String photoLocation = storageProperties.getPhotoLocation();
        Path fileNameAndPath = Paths.get(photoLocation, "test.jpg");
        byte[] byteArray = Files.readAllBytes(fileNameAndPath);
        Photo photo = storageService.uploadPhotos("test.jpg", byteArray);
        assertNotNull(photo);
    }

    @Test
    void testDeletePhoto_NotExistingFile() {
        Folder folder = folderRepository.save(new Folder());
        Photo photo = photoRepository.save(new Photo(random.nextInt(), folder, "NotExistingFile", "description"));
        boolean isDeleted = storageService.deletePhoto(photo);
        assertFalse(isDeleted);

        photoRepository.delete(photo);
        folderRepository.delete(folder);
    }

    @Test
    void testDeletePhoto_ExistingFile() {
        Folder folder = folderRepository.save(new Folder());
        Photo photo = photoRepository.save(new Photo(random.nextInt(), folder, "testDeletePhoto_ExistingFile.jpg", "description"));

        s3Client.putObject(new PutObjectRequest(bucketName,
                String.valueOf(photo.getIdentifier()),
                new ByteArrayInputStream("test".getBytes()),
                null));

        boolean isDeleted = storageService.deletePhoto(photo);
        assertTrue(isDeleted);

        photoRepository.delete(photo);
        folderRepository.delete(folder);
    }

    @Test
    void testLoadPhotoAsResource_EmptyPhotoName() {
        Folder folder = folderRepository.save(new Folder());
        Photo photo = photoRepository.save(new Photo(random.nextInt(), folder, "", "description"));
        byte[] byteArray = storageService.loadPhotoAsResource(photo);
        assertNull(byteArray);

        photoRepository.delete(photo);
        folderRepository.delete(folder);
    }

    @Test
    void testLoadPhotoAsResource_NotExistingPhoto() {
        Folder folder = folderRepository.save(new Folder());
        Photo photo = photoRepository.save(new Photo(random.nextInt(), folder, "name", "description"));
        byte[] byteArray = storageService.loadPhotoAsResource(photo);
        assertNull(byteArray);

        photoRepository.delete(photo);
        folderRepository.delete(folder);
    }

    @Test
    void testLoadPhotoAsResource_ExistingPhoto() {
        Folder folder = folderRepository.save(new Folder());
        Photo photo = photoRepository.save(new Photo(random.nextInt(), folder, "test.jpg", "description"));

        s3Client.putObject(new PutObjectRequest(bucketName,
                String.valueOf(photo.getIdentifier()),
                new ByteArrayInputStream("test".getBytes()),
                null));

        byte[] byteArray = storageService.loadPhotoAsResource(photo);
        assertNotNull(byteArray);

        photoRepository.delete(photo);
        folderRepository.delete(folder);
    }
}