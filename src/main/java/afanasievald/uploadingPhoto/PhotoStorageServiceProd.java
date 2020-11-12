package afanasievald.uploadingPhoto;

import afanasievald.databaseEntity.Photo;
import afanasievald.uploadingPhoto.image.ImageRotation;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.URLConnection;
import java.util.*;

@Profile({"prod", "testprod"})
@Service
public class PhotoStorageServiceProd implements StorageService {

    @Autowired
    private S3Service s3Service;

    @NotNull
    String bucketName;

    @NotNull
    private final Logger LOGGER = LogManager.getLogger(PhotoStorageServiceProd.class.getName());
    
    @Autowired
    public PhotoStorageServiceProd(StorageProperties properties){
        this.bucketName = properties.getBucketName();
    }

    private AmazonS3 getClient(){
        AmazonS3 s3Client = null;
        if (s3Service != null) {
            s3Client = s3Service.getClient();
            return s3Client;
        }
        return s3Client;
    }

    @Override
    public Photo uploadPhotos(@NotNull String fileName, @NotNull byte[] byteArray){
        Photo photo = new Photo();
        try {
            InputStream is = new BufferedInputStream(new ByteArrayInputStream(byteArray));
            String mimeType = URLConnection.guessContentTypeFromStream(is);
            if (mimeType == null || !mimeType.startsWith("image/")) {
                LOGGER.info(String.format("File %s isn't image", fileName));
                return null;
            }

            byte[] normalizedByteArray = ImageRotation.normalizeOrientation(byteArray);
            photo.setIdentifier(Arrays.hashCode(byteArray) + (new Date()).hashCode());
            String fileExtension = Objects.requireNonNull(fileName).split("\\.")[1];
            photo.setName(String.format("%s.%s", photo.getIdentifier(), fileExtension));

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(normalizedByteArray.length);
            getClient().putObject(new PutObjectRequest(bucketName,
                    String.valueOf(photo.getIdentifier()),
                    new ByteArrayInputStream(normalizedByteArray),
                    metadata));

        } catch (SdkClientException | IOException e) {
            LOGGER.error(e);
            return null;
        }
        return photo;
    }

    @Override
    public boolean deletePhoto(@NotNull Photo photo){
        try {
            if (!getClient().doesObjectExist(bucketName, String.valueOf(photo.getIdentifier()))) {
                LOGGER.info(String.format("Photo with identifier %d doesn't exist in bucket %s",
                        photo.getIdentifier(), bucketName));
                return false;
            }
            getClient().deleteObject(bucketName, String.valueOf(photo.getIdentifier()));

        } catch (AmazonServiceException e) {
            LOGGER.error(e);
            return false;
        }  catch (SdkClientException e)  {
            LOGGER.error(String.format("Photo with identifier %d isn't deleted from bucket %s",
                    photo.getIdentifier(), bucketName), e);
            return false;
        }

        return true;
    }

    @Override
    public byte[] loadPhotoAsResource(@NotNull Photo photo){
        String fileName = photo.getName();
        if (fileName.isEmpty()) {
            LOGGER.info(String.format("Photo %s is empty", fileName));
            return null;
        }

        try {
            if (!getClient().doesObjectExist(bucketName, String.valueOf(photo.getIdentifier()))) {
                LOGGER.info(String.format("Photo with identifier %d doesn't exist in bucket %s",
                        photo.getIdentifier(), bucketName));
                return null;
            }

            S3Object object = getClient().getObject(new GetObjectRequest(bucketName, String.valueOf(photo.getIdentifier())));

            return object.getObjectContent().readAllBytes();
        } catch (SdkClientException | IOException e) {
            LOGGER.error(e);
            return null;
        }
    }
}
