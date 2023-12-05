package com.kse.wmsv2.common.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Service
public class AwsS3 {

    private S3Client s3Client;

    @Value("${application.bucket.name}")
    private String bucket;

    /**
     * コンストラクタ
     */
    public AwsS3() {
        createS3Client();
    }

    // singleton より作成
    static private AwsS3 instance = null;

    public static AwsS3 getInstance() {
        if (instance == null) {
            return new AwsS3();
        } else {
            return instance;
        }
    }

    /**
     * S3Client生成
     */
    private void createS3Client() {
        this.s3Client = S3Client.builder().build();
    }

    public void upload(byte[] fileByte, String key) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(this.bucket)
                .key(key)
                .build();
        this.s3Client.putObject(putObjectRequest,
                RequestBody.fromBytes(fileByte));
    }

    public int uploadPdf(byte[] fileByte, String keyName) {
        int uploadCnt = 0;

        if (keyName.endsWith(".pdf")) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(this.bucket)
                    .key(keyName)
                    .build();
            this.s3Client.putObject(putObjectRequest,
                    RequestBody.fromBytes(fileByte));
            uploadCnt++;
        }
        return uploadCnt;
    }

    public byte[] get(String key) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(this.bucket)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> s3Object = this.s3Client.getObject(getObjectRequest);
        byte[] objectBytes = s3Object.readAllBytes();
        return objectBytes;
    }

    public void delete(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(this.bucket)
                .key(key)
                .build();
        this.s3Client.deleteObject(deleteObjectRequest);
    }
}

