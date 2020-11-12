package afanasievald.uploadingPhoto;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import afanasievald.databaseEntity.Photo;
import afanasievald.uploadingPhoto.image.ImageRotation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile({"dev", "testdev"})
@Service
public class PhotoStorageServiceDev implements StorageService {
    @NotNull
    private final String photoLocation;

    @NotNull
    private final Logger LOGGER = LogManager.getLogger(PhotoStorageServiceDev.class.getName());

    @Autowired
    public PhotoStorageServiceDev(StorageProperties properties) throws IOException {
        this.photoLocation = properties.getPhotoLocation();
        Path photoLocationPath = Paths.get(this.photoLocation);
        if (!Files.exists(photoLocationPath)) {
            Files.createDirectories(photoLocationPath);
        } else {
            if (!Files.isDirectory(photoLocationPath)) {
                LOGGER.error(String.format("Application is closed, because photoLocationPath %s isn't directory", this.photoLocation));
                throw new IllegalArgumentException(String.format("Application is closed, because photoLocationPath %s isn't directory", this.photoLocation));
            }
        }
    }

    @Override
    public Photo uploadPhotos(@NotNull String fileName, @NotNull byte[] byteArray){
        Path fileNameAndPath = Paths.get(photoLocation, fileName);
        Photo photo = new Photo();
        try {
            InputStream is = new BufferedInputStream(new ByteArrayInputStream(byteArray));
            String mimeType = URLConnection.guessContentTypeFromStream(is);

            if (mimeType == null || !mimeType.startsWith("image/")) {
                LOGGER.info(String.format("File %s isn't image", fileNameAndPath));
                return null;
            }

            byte[] normalizedByteArray = ImageRotation.normalizeOrientation(byteArray);
            photo.setIdentifier(Arrays.hashCode(byteArray) + (new Date()).hashCode());
            String fileExtension = Objects.requireNonNull(fileName).split("\\.")[1];
            photo.setName(String.format("%s.%s", photo.getIdentifier(), fileExtension));
            Path newFileNameAndPath = Paths.get(photoLocation, photo.getName());
            Files.write(newFileNameAndPath, normalizedByteArray);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
        return photo;
    }

    @Override
    public boolean deletePhoto(@NotNull Photo photo){
        Path fileNameAndPath = Paths.get(photoLocation, photo.getName());
        if (!Files.exists(fileNameAndPath)) {
            LOGGER.info(String.format("Photo %s doesn't exist", fileNameAndPath.toString()));
            return false;
        }
        try {
            Files.delete(fileNameAndPath);

        } catch (IOException e) {
            LOGGER.error(String.format("Photo %s with identifier %d isn't deleted from disk",
                    fileNameAndPath.toString(), photo.getIdentifier()), e);
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
            Path filePath = Paths.get(photoLocation, fileName);
            if (!Files.exists(filePath)) {
                LOGGER.info(String.format("File %s doesn't exist", filePath));
                return null;
            }

            if (!Files.isReadable(filePath)) {
                LOGGER.info(String.format("File %s isn't readable", filePath));
                return null;
            }

            return Files.readAllBytes(filePath);
        } catch (InvalidPathException | IOException e) {
            LOGGER.error(e);
            return null;
        }
    }
}
