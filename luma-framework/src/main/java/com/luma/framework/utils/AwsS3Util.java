package com.luma.framework.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.luma.common.exception.BaseException;
import com.luma.framework.config.AwsS3Config;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
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
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class AwsS3Util {

    private final static StsClient STS_CLIENT = SpringUtil.getBean(StsClient.class);
    private final static S3Client CLIENT = SpringUtil.getBean(S3Client.class);
    private final static S3Presigner PRESIGNER = SpringUtil.getBean(S3Presigner.class);

    public static AwsS3Config getConfig(){
        return SpringUtil.getBean(AwsS3Config.class);
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
     * 获取minio中文件临时访问路径
     * @param bucket 桶
     * @param objectName 文件全路径
     * @param second 有效时长 秒
     * @return
     */
    public static String getOpenUrl(String bucket, String objectName, int second){
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectName)
                    .build();
            return PRESIGNER.presignGetObject(
                    GetObjectPresignRequest.builder()
                            .getObjectRequest(getObjectRequest)
                            .signatureDuration(Duration.ofSeconds(second))
                            .build()
            ).url().toString();
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage());
        }
    }

    public static void putObject(String bucket, String objectName, InputStream inputStream){
        try {
            putObject(bucket, objectName, inputStream, -1, 5 * 1024 * 1024, -1, "application/octet-stream");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 添加压缩文件
     * @param bucket 桶
     * @param objectName 文件全路径
     * @param inputStream 文件流
     * @return
     */
    public static void putZipObject(String bucket, String objectName, InputStream inputStream){
        try {

            putObject(bucket, objectName, inputStream, -1, 5 * 1024 * 1024, -1, "application/zip");

        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 获取文件流
     * @param bucket 桶
     * @param objectName 文件全路径
     * @return
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
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 获取文件属性，而不获取文件本身
     * @param bucket 桶
     * @param objectName 文件全路径
     * @return
     */
    public static GetObjectAttributesResponse getObjectInfo(String bucket, String objectName){
        return CLIENT.getObjectAttributes(GetObjectAttributesRequest.builder()
                        .bucket(bucket)
                        .key(objectName)
                        .objectAttributes(ObjectAttributes.E_TAG, ObjectAttributes.OBJECT_SIZE)
                .build());
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
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 大文件分片上传从
     * @param bucket
     * @param objectName
     * @param inputStream
     * @param partCount
     * @param partSize
     * @param objectSize
     * @throws IOException
     * @throws IOException
     */
    protected static void putObject(String bucket, String objectName, InputStream inputStream, int partCount, long partSize, long objectSize, String contentType) throws IOException {
        final BufferedInputStream data =
                (inputStream instanceof BufferedInputStream)
                        ? (BufferedInputStream) inputStream
                        : new BufferedInputStream(inputStream);

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

    private static long getAvailableSize(Object data, long expectedReadSize)
            throws IOException {
        if (!(data instanceof BufferedInputStream)) {
            throw new RuntimeException(
                    "data must be BufferedInputStream. This should not happen.  "
                            + "Please report to https://github.com/minio/minio-java/issues/",
                    null);
        }

        BufferedInputStream stream = (BufferedInputStream) data;
        stream.mark((int) expectedReadSize);

        byte[] buf = new byte[16384]; // 16KiB buffer for optimization
        long totalBytesRead = 0;
        while (totalBytesRead < expectedReadSize) {
            long bytesToRead = expectedReadSize - totalBytesRead;
            if (bytesToRead > buf.length) bytesToRead = buf.length;
            int bytesRead = stream.read(buf, 0, (int) bytesToRead);
            if (bytesRead < 0) break; // reached EOF
            totalBytesRead += bytesRead;
        }

        stream.reset();
        return totalBytesRead;
    }

}
