package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性，前缀 sky.jwt
 */
@Component
@ConfigurationProperties(prefix = "sky.jwt")
@Data
public class JwtProperties {

    /** 管理端员工生成令牌使用的密钥 */
    private String adminSecretKey;

    /** 管理端令牌过期时间（毫秒） */
    private long adminTtl;

    /** 管理端令牌在请求头中的名称 */
    private String adminTokenName;

    /** 用户端生成令牌使用的密钥 */
    private String userSecretKey;

    /** 用户端令牌过期时间（毫秒） */
    private long userTtl;

    /** 用户端令牌在请求头中的名称 */
    private String userTokenName;
}
