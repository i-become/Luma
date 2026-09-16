package com.luma.framework.config;

import com.luma.framework.utils.AwsS3Util;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesRequest;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesResponse;
import software.amazon.awssdk.services.s3.model.ObjectAttributes;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * 亚马逊s3配置
 */
@Data
@Log4j2
@Configuration
@ConditionalOnProperty(name = "aws.enable")
public class AwsS3Config {

    @Value("${aws.access-key-id}")
    private String accessKeyId;

    @Value("${aws.secret-access-key}")
    private String secretAccessKey;

    @Value("${aws.endpoint}")
    private String endPoint;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.provider}")
    private String provider;

    @Value("${aws.bucket}")
    private String bucket;

    @Value("${aws.nginx-url}")
    private String nginxUrl;

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

    /**
     * 初始化方法
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init(){
        try {
            log.info("系统初始化 - 开始");
            // 初始化对象存储
            initMinio();
        }catch (Exception e){
            log.error("系统初始化 - 失败", e);
//            System.exit(0);
        }
    }

    /**
     * 初始化对象存储
     */
    public void initMinio(){
        // 判断需要的对象存储bucket是否创建，没有则自动创建
        List<Bucket> buckets = AwsS3Util.listBuckets();
        for(Bucket bk : buckets){
            if (bucket.equals(bk.name())){
                log.info("系统初始化 - 对象存储bucket已存在，无需重复创建");
                return;
            }
        }
        log.info("系统初始化 - 对象存储bucket不存在，创建bucket:{}", bucket);
        AwsS3Util.createBucket(bucket);
        log.info("系统初始化 - 对象存储bucket成功");
    }

}
