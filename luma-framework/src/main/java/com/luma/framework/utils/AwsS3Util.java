package com.luma.framework.utils;

import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.luma.common.exception.BaseException;
import com.luma.framework.config.AwsS3Config;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * s3协议的对象存储工具类
 * @author i-become
 */
@Slf4j
public class AwsS3Util {

    private final static StsClient STS_CLIENT = SpringUtil.getBean(StsClient.class);
    private final static S3Client CLIENT = SpringUtil.getBean(S3Client.class);
    private final static S3Presigner PRESIGNER = SpringUtil.getBean(S3Presigner.class);
    public static final AwsS3Config CONFIG = SpringUtil.getBean(AwsS3Config.class);


    /**
     * 创建桶
     * @param bucketName 桶名称
     */
    public static void createBucket(String bucketName){
        CLIENT.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
    }

    /**
     * 获取现有的桶
     * @return
     */
    public static List<Bucket> listBuckets(){
        return CLIENT.listBuckets().buckets();
    }

    /**
     * 获取临时访问凭证
     * @param time 有效时间
     * @return
     */
    public static AwsSessionCredentials createUploadCertificate(int time){
        // 创建 AssumeRole 请求
        AssumeRoleRequest assumeRoleRequest = AssumeRoleRequest.builder()
                .roleSessionName("session-" + System.currentTimeMillis())
                .durationSeconds(time)
                .build();
        AssumeRoleResponse assumeRoleResponse = STS_CLIENT.assumeRole(assumeRoleRequest);
        return AwsSessionCredentials.create(
                assumeRoleResponse.credentials().accessKeyId(),
                assumeRoleResponse.credentials().secretAccessKey(),
                assumeRoleResponse.credentials().sessionToken());
    }

    /**
     * 获取minio中文件临时访问路径，默认桶
     * @param objectName 文件全路径
     * @param second 有效时长 秒
     */
    public static String getOpenUrl(String objectName, int second){
        return getOpenUrl(CONFIG.getBucket(), objectName, second);
    }

    /**
     * 获取minio中文件临时访问路径
     * @param bucket 桶
     * @param objectName 文件全路径
     * @param second 有效时长 秒
     */
    public static String getOpenUrl(String bucket, String objectName, int second){
        return getOpenUrlForNginx(bucket, objectName, second);
    }

    public static String getOpenUrlForNginx(String bucket, String objectName, int second){
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectName)
                .build();
        String url = PRESIGNER.presignGetObject(
                GetObjectPresignRequest.builder()
                        .getObjectRequest(getObjectRequest)
                        .signatureDuration(Duration.ofSeconds(second))
                        .build()
        ).url().toString();
        return replaceNginxUrl(url, CONFIG.getNginxUrl());
    }

    private static String replaceNginxUrl(String originalUrl, String nginxUrlStr){
        try {
            // 解析原始预签名URL
            URL originalPresignedUrl = new URL(originalUrl);

            // 解析nginx主机URL
            URL nginxUrl = new URL(nginxUrlStr);

            // 直接使用nginx的协议、主机、端口，但保留原始URL的路径和查询参数
            URL newUrl = new URL(
                    nginxUrl.getProtocol(),
                    nginxUrl.getHost(),
                    nginxUrl.getPort(),
                    originalPresignedUrl.getFile()  // 包含路径和查询参数
            );

            return newUrl.toString();
        } catch (Exception e) {
            log.error("替换URL Host失败", e);
            return originalUrl;
        }
    }

    /**
     * 获取minio中文件临时访问路径（只有路径和查询参数，没有协议和ip和端口），使用默认桶
     * @param objectName 文件全路径
     * @param second 有效时长 秒
     */
    public static String getOpenUrlPathAndQuery(String objectName, int second){
        String openUrl = getOpenUrl(CONFIG.getBucket(), objectName, second);
        URL url = URLUtil.toUrlForHttp(openUrl);
        return String.format("%s?%s", url.getPath(), url.getQuery());
    }

    /**
     * 获取minio中文件临时访问路径（只有路径和查询参数，没有协议和ip和端口）
     * @param bucket 桶
     * @param objectName 文件全路径
     * @param second 有效时长 秒
     */
    public static String getOpenUrlPathAndQuery(String bucket, String objectName, int second){
        String openUrl = getOpenUrl(bucket, objectName, second);
        URL url = URLUtil.toUrlForHttp(openUrl);
        return String.format("%s?%s", url.getPath(), url.getQuery());
    }

    public static void putObject(String bucket, String objectName, byte[] bytes){
        try {
            CLIENT.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(objectName)
                            .build(),
                    RequestBody.fromBytes(bytes)
            );
        } catch (AwsServiceException e) {
            log.error("文件存储失败", e);
            throw new BaseException("文件存储失败");
        }
    }

    /**
     * 添加文件到默认的桶中
     * @param objectName 文件名称
     * @param bytes 字节
     */
    public static void putObject(String objectName, byte[] bytes){
        putObject(CONFIG.getBucket(), objectName, bytes);
    }

    /**
     * 添加文件到默认的桶中
     * @param objectName 文件名称
     * @param inputStream 文件流
     */
    public static void putObject(String objectName, InputStream inputStream){
        putObject(CONFIG.getBucket(), objectName, inputStream);
    }

    public static void putObject(String bucket, String objectName, InputStream inputStream){
        try (InputStream in = inputStream) {
            putObject(bucket, objectName, in, -1, 5 * 1024 * 1024, -1, "application/octet-stream");
        } catch (Exception e) {
            log.error("文件存储失败", e);
            throw new BaseException("文件存储失败");
        }
    }

    /**
     * 添加压缩文件 默认桶
     * @param objectName 文件全路径
     * @param inputStream 文件流
     */
    public static void putZipObject(String objectName, InputStream inputStream){
        putZipObject(CONFIG.getBucket(), objectName, inputStream);
    }

    /**
     * 添加压缩文件
     * @param bucket 桶
     * @param objectName 文件全路径
     * @param inputStream 文件流
     */
    public static void putZipObject(String bucket, String objectName, InputStream inputStream){
        try {

            putObject(bucket, objectName, inputStream, -1, 5 * 1024 * 1024, -1, "application/zip");

        }catch (Exception e){
            log.error("文件存储失败", e);
            throw new BaseException("文件存储失败");
        }
    }


    /**
     * 获取文件流，默认桶
     *  * <p>
     *  * 注意：调用方必须在使用完成后调用 {@link InputStream#close()}，
     *  * 否则会导致 S3Client 连接池耗尽！
     *  *
     *  * 建议使用 try-with-resources：
     *  * <pre>
     *  * try (InputStream in = AwsS3Util.getObject(...)) {
     *  *     // 处理流
     *  * }
     *  * </pre>
     * @param objectName 文件全路径
     * @return InputStream 调用方必须 close()
     */
    public static InputStream getObject(String objectName){
        return getObject(CONFIG.getBucket(), objectName);
    }

    /**
     * 获取文件流
     *  * <p>
     *  * 注意：调用方必须在使用完成后调用 {@link InputStream#close()}，
     *  * 否则会导致 S3Client 连接池耗尽！
     *  *
     *  * 建议使用 try-with-resources：
     *  * <pre>
     *  * try (InputStream in = AwsS3Util.getObject(...)) {
     *  *     // 处理流
     *  * }
     *  * </pre>
     * @param bucket 桶
     * @param objectName 文件全路径
     * @return InputStream 调用方必须 close()
     */
    public static InputStream getObject(String bucket, String objectName){
        try {
            return CLIENT.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(objectName)
                            .build()
            );
        }catch (Exception e){
            log.error("文件获取失败", e);
            throw new BaseException("文件获取失败");
        }
    }

    /**
     * 获取文件属性，而不获取文件本身，使用默认桶
     * @param objectName 文件全路径
     * @return
     */
    public static GetObjectAttributesResponse getObjectInfo(String objectName, ObjectAttributes attributes){
        return CLIENT.getObjectAttributes(GetObjectAttributesRequest.builder()
                .bucket(CONFIG.getBucket())
                .key(objectName)
                .objectAttributes(attributes)
                .build());
    }

    /**
     * 获取文件属性，而不获取文件本身
     * @param bucket 桶
     * @param objectName 文件全路径
     * @return
     */
    public static GetObjectAttributesResponse getObjectInfo(String bucket, String objectName, ObjectAttributes attributes){
        return CLIENT.getObjectAttributes(GetObjectAttributesRequest.builder()
                .bucket(bucket)
                .key(objectName)
                .objectAttributes(attributes)
                .build());
    }

    /**
     * 复制文件，默认桶
     * @param sourceName 源文件路径
     * @param targetName 目标文件路径
     */
    public static CopyObjectResponse copyObject(String sourceName, String targetName){
        return copyObject(CONFIG.getBucket(), sourceName, CONFIG.getBucket(), targetName);
    }

    /**
     * 复制文件
     * @param sourceBucket 源文件桶
     * @param sourceName 源文件路径
     * @param targetBucket 目标文件桶
     * @param targetName 目标文件路径
     * @return
     */
    public static CopyObjectResponse copyObject(String sourceBucket, String sourceName, String targetBucket, String targetName){
        try {
            return CLIENT.copyObject(CopyObjectRequest.builder()
                    .sourceBucket(sourceBucket)
                    .sourceKey(sourceName)
                    .destinationBucket(targetBucket)
                    .destinationKey(targetName)
                    .build());
        }catch (Exception e){
            log.error("文件复制失败", e);
            throw new BaseException("文件复制失败");
        }
    }

    /**
     * 大文件分片上传从
     * @param bucket 桶名称
     * @param objectName 文件全路径
     * @param inputStream 文件流
     * @param partCount part数量
     * @param partSize  part大小
     * @param objectSize 文件大小
     */
    protected static void putObject(String bucket, String objectName, InputStream inputStream, int partCount, long partSize, long objectSize, String contentType) throws IOException {
        try (BufferedInputStream data =
                     (inputStream instanceof BufferedInputStream bis)
                             ? bis
                             : new BufferedInputStream(inputStream)) {

            String uploadId = null;
            long uploadedSize = 0L;

            try {
                List<CompletedPart> partETags = new ArrayList<>();
                for (int partNumber = 1; partNumber <= partCount || partCount < 0; partNumber++) {
                    long availableSize = partSize;
                    if (partCount > 0) {
                        if (partNumber == partCount) {
                            availableSize = objectSize - uploadedSize;
                        }
                    } else {
                        availableSize = getAvailableSize(data, partSize + 1);

                        // If availableSize is less or equal to partSize, then we have reached last
                        // part.
                        if (availableSize <= partSize) {
                            partCount = partNumber;
                        } else {
                            availableSize = partSize;
                        }
                    }

                    if (partCount == 1) {
                        CLIENT.putObject(PutObjectRequest.builder().bucket(bucket).key(objectName).contentType(contentType).build(), RequestBody.fromInputStream(data, data.available()));
                        return;
                    }

                    if (uploadId == null) {
                        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                                .bucket(bucket)
                                .key(objectName)
                                .contentType(contentType)
                                .build();
                        CreateMultipartUploadResponse response = CLIENT.createMultipartUpload(createMultipartUploadRequest);
                        uploadId = response.uploadId();
                    }

                    UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                            .bucket(bucket)
                            .key(objectName)
                            .uploadId(uploadId)
                            .partNumber(partNumber)
                            .contentLength(availableSize)
                            .build();
                    // 上传分块
                    partETags.add(CompletedPart.builder().eTag(CLIENT.uploadPart(uploadPartRequest, RequestBody.fromInputStream(data, availableSize)).eTag()).partNumber(partNumber).build());

                    uploadedSize += availableSize;
                }

                // 完成分块上传
                CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                        .bucket(bucket)
                        .key(objectName)
                        .uploadId(uploadId)
                        .multipartUpload(mp -> mp.parts(partETags))
                        .build();

                CLIENT.completeMultipartUpload(completeMultipartUploadRequest);
            } catch (Exception e) {
                if (uploadId != null) {
                    CLIENT.abortMultipartUpload(
                            AbortMultipartUploadRequest.builder()
                                    .bucket(bucket)
                                    .key(objectName)
                                    .uploadId(uploadId)
                                    .build()
                    );
                }
                throw e;
            }
        }
    }

    private static long getAvailableSize(Object data, long expectedReadSize)
            throws IOException {
        if (!(data instanceof BufferedInputStream stream)) {
            throw new RuntimeException(
                    "data must be BufferedInputStream. This should not happen.  "
                            + "Please report to https://github.com/minio/minio-java/issues/",
                    null);
        }

        stream.mark((int) expectedReadSize);

        // 用于优化的 16KiB 缓冲区
        byte[] buf = new byte[16384];
        long totalBytesRead = 0;
        while (totalBytesRead < expectedReadSize) {
            long bytesToRead = expectedReadSize - totalBytesRead;
            if (bytesToRead > buf.length) bytesToRead = buf.length;
            int bytesRead = stream.read(buf, 0, (int) bytesToRead);
            // 达到 EOF
            if (bytesRead < 0) break;
            totalBytesRead += bytesRead;
        }

        stream.reset();
        return totalBytesRead;
    }

    /**
     * 删除文件
     * @param bucket 文件桶
     * @param objectName 文件路径
     */
    public static void deleteObject(String bucket, String objectName) {
        try {
            CLIENT.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(objectName)
                            .build()
            );
        } catch (AwsServiceException e) {
            log.error("文件删除失败", e);
            throw new BaseException("文件删除失败");
        }
    }

    /**
     * 删除文件
     * @param objectName 文件路径
     */
    public static void deleteObject(String objectName) {
        deleteObject(CONFIG.getBucket(), objectName);
    }

    /**
     * 删除多个文件，默认桶
     * @param objectNames 文件路径列表
     */
    public static void deleteObjects(List<String> objectNames) {
        deleteObjects(CONFIG.getBucket(), objectNames);
    }

    /**
     * 删除多个文件
     * @param bucket 文件桶
     * @param objectNames 文件路径列表
     */
    public static void deleteObjects(String bucket, List<String> objectNames) {
        try {
            CLIENT.deleteObjects(
                    DeleteObjectsRequest.builder()
                            .bucket(bucket)
                            .delete(Delete.builder()
                                    .objects(objectNames.stream().map(objectName -> ObjectIdentifier.builder().key(objectName).build()).collect(Collectors.toList()))
                                    // 是否返回详细结果
                                    .quiet(false)
                                    .build())
                            .build()
            );
        } catch (AwsServiceException e) {
            log.error("文件删除失败", e);
            throw new BaseException("文件删除失败");
        }
    }

}

