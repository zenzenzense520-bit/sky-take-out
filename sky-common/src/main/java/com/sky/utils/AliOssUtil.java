package com.sky.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 阿里云 OSS 文件上传工具
 * 由配置类创建单例 Bean，避免重复创建客户端
 */
public class AliOssUtil {

    private final String endpoint;
    private final String accessKeyId;
    private final String accessKeySecret;
    private final String bucketName;

    public AliOssUtil(String endpoint, String accessKeyId, String accessKeySecret, String bucketName) {
        this.endpoint = endpoint;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.bucketName = bucketName;
    }

    /**
     * 上传文件，按日期分目录并生成随机文件名，返回可访问 URL
     *
     * @param bytes      文件字节数组
     * @param originalName 原始文件名（用于提取扩展名）
     */
    public String upload(byte[] bytes, String originalName) {
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String objectName = datePath + "/" + UUID.randomUUID().toString().replace("-", "") + extension;

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(bytes.length);
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes), metadata);
        } finally {
            ossClient.shutdown();
        }

        return "https://" + bucketName + "." + endpoint.replace("https://", "") + "/" + objectName;
    }
}
