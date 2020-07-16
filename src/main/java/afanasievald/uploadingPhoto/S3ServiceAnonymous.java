package afanasievald.uploadingPhoto;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import io.findify.s3mock.S3Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import java.util.List;
import java.util.Optional;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.stereotype.Service;

@Profile("testprod")
@Service
public class S3ServiceAnonymous implements S3Service{
    private AmazonS3 amazonS3;
    @Autowired
    public S3ServiceAnonymous(StorageProperties properties){
        String bucketName = properties.getBucketName();

        Region usEast2 = Region.getRegion(Regions.US_EAST_2);
            S3Mock api = new S3Mock.Builder().withPort(8001).withInMemoryBackend().build();
            api.start();
            AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration(properties.getServiceEndpoint(), usEast2.getName());
        amazonS3 = AmazonS3ClientBuilder
                    .standard()
                    .withPathStyleAccessEnabled(true)
                    .withEndpointConfiguration(endpoint)
                    .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                    .build();

        List<Bucket> listOfBuckets = amazonS3.listBuckets();
        Optional<Bucket> bucket = listOfBuckets.stream().filter(x->x.getName().compareTo(bucketName)==0).findFirst();

        if (!bucket.isPresent()) {
            amazonS3.createBucket(bucketName);
        }
    }

    public AmazonS3 getClient(){
        return amazonS3;
    }
}
