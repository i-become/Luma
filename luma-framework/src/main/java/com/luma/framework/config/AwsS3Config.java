package com.luma.framework.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesRequest;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesResponse;
import software.amazon.awssdk.services.s3.model.ObjectAttributes;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 亚马逊s3配置
 */
@Data
@Configuration
public class AwsS3Config {

    @Value("${aws.access-key-id}")
    private String accessKeyId;

    @Value("${aws.secret-access-key}")
    private String secretAccessKey;

    @Value("${aws.end-point}")
    private String endPoint;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.object-key-prefix}")
    private String objectKeyPrefix;

    @Value("${aws.provider}")
    private String provider;

    /**
     * S3客户端
     * @return
     * @throws URISyntaxException
     */
    @Bean
    public S3Client s3Client() throws URISyntaxException {
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .endpointOverride(new URI(endPoint))
                .region(Region.of(region))
                .build();
    }

    /**
     * S3预签名
     * @return
     * @throws URISyntaxException
     */
    @Bean
    public S3Presigner s3Presigner() throws URISyntaxException {
        return S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .endpointOverride(new URI(endPoint))
                .region(Region.of(region))
                .build();
    }

    @Bean
    public StsClient stsClient() throws URISyntaxException {
        return StsClient.builder()
                .endpointOverride(new URI(endPoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();
    }

}
