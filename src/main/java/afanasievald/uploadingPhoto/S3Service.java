package afanasievald.uploadingPhoto;

import com.amazonaws.services.s3.AmazonS3;

public interface S3Service {
    AmazonS3 getClient();
}
