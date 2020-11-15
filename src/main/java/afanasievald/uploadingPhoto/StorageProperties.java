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

    private String bucketName;

    private String serviceEndpoint;

    private String region;

    private String port;

    private  String datasourceUrl;

    private String datasourceUserName;

    private String datasourcePassword;

    public StorageProperties() {
    }

    public StorageProperties(String photoLocation, String bucketName) {
        this.photoLocation = photoLocation;
        this.bucketName = bucketName;
    }

    public @NotNull String getPhotoLocation() {
        return photoLocation;
    }

    public void setPhotoLocation(@NotNull String photoLocation) {
        this.photoLocation = photoLocation;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getServiceEndpoint() {
        return serviceEndpoint;
    }

    public void setServiceEndpoint(String serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDatasourceUrl() {
        return datasourceUrl;
    }

    public void setDatasourceUrl(String datasourceUrl) {
        this.datasourceUrl = datasourceUrl;
    }

    public String getDatasourceUserName() {
        return datasourceUserName;
    }

    public void setDatasourceUserName(String datasourceUserName) {
        this.datasourceUserName = datasourceUserName;
    }

    public String getDatasourcePassword() {
        return datasourcePassword;
    }

    public void setDatasourcePassword(String datasourcePassword) {
        this.datasourcePassword = datasourcePassword;
    }
}
