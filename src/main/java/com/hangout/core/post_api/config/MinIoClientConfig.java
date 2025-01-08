package com.hangout.core.post_api.config;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hangout.core.post_api.exceptions.ConnectionFailed;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class MinIoClientConfig {
    @Value("${minio.url}")
    private String serverUrl;
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;
    @Value("${hangout.media.upload-bucket}")
    private String uploadBucket;

    @Bean
    MinioClient minioClient() {
        try {
            log.info("connecting with Minio/s3");
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(serverUrl)
                    .credentials(accessKey, secretKey)
                    .build();
            boolean isBucketExist = minioClient
                    .bucketExists(BucketExistsArgs.builder().bucket(uploadBucket).build());
            if (!isBucketExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(uploadBucket).build());
            } else {
                log.debug("Bucket {} exists, so skipping creation of the bucket", uploadBucket);
            }
            log.info("Successfully connected with Minio/s3");
            return minioClient;
        } catch (MinioException ex) {
            log.error("Error occured in connecting with Minio/S3, exception: {}", ex);
            log.debug("Http trace:{}", ex.httpTrace());
            throw new ConnectionFailed("failed to establish connection with Minio/s3");
        } catch (InvalidKeyException | NoSuchAlgorithmException | IllegalArgumentException | IOException ex) {
            log.error("Minio connection failed for wrong connection parameters", ex);
            throw new ConnectionFailed("Failed to connect with Minio/s3 due to wrong connection parameters");
        }
    }
}
