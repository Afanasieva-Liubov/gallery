package afanasievald.uploadingPhoto;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "server")
public class StorageProperties {

    /**
     * Folder location for storing files
     */
    private String photoLocation;

    private String accessKey;

    private String secretKey;

    public StorageProperties() {
    }

    public StorageProperties(@NotNull String photoLocation, String accessKey, String secretKey) {
        this.photoLocation = photoLocation;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public @NotNull String getPhotoLocation() {
        return photoLocation;
    }

    public void setPhotoLocation(@NotNull String photoLocation) {
        this.photoLocation = photoLocation;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}