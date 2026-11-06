package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云 OSS 配置：创建工具类单例
 */
@Configuration
public class OssConfiguration {

    @Bean
    public AliOssUtil aliOssUtil(AliOssProperties properties) {
        return new AliOssUtil(
                properties.getEndpoint(),
                properties.getAccessKeyId(),
                properties.getAccessKeySecret(),
                properties.getBucketName());
    }
}
