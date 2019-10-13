package net.eightlives.mindy.config;

import net.eightlives.mindy.config.custom.ImageBucketConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class S3ImageBucketConfig {

    private final ImageBucketConfig imageBucketConfig;

    public S3ImageBucketConfig(ImageBucketConfig imageBucketConfig) {
        this.imageBucketConfig = imageBucketConfig;
    }

    @Bean
    public S3Client getS3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                imageBucketConfig.getAccessKeyId(),
                imageBucketConfig.getSecretAccessKey());
        return S3Client.builder()
                .endpointOverride(URI.create(imageBucketConfig.getUrl()))
                .region(Region.of(imageBucketConfig.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
