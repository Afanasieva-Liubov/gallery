package afanasievald.uploadingPhoto;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Profile("prod")
@Service
public class S3ServiceAmazon implements S3Service{
    private AmazonS3 amazonS3;
    @Autowired
    public S3ServiceAmazon(StorageProperties properties){
        String region = properties.getRegion();
        Region usEast2 = Region.getRegion(Regions.valueOf(region));

        amazonS3 = AmazonS3ClientBuilder
                    .standard()
                    .withCredentials(new EnvironmentVariableCredentialsProvider())
                    .withRegion(usEast2.getName())
                    .build();

        List<Bucket> listOfBuckets = amazonS3.listBuckets();
        String bucketName = properties.getBucketName();
        Optional<Bucket> bucket = listOfBuckets.stream().filter(x->x.getName().compareTo(bucketName)==0).findFirst();

        if (!bucket.isPresent()) {
            amazonS3.createBucket(bucketName);
        }
    }

    public AmazonS3 getClient(){
        return amazonS3;
    }
}
