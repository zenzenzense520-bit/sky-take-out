package com.sky.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类：生成与解析令牌（基于 jjwt 0.9.1）
 */
public class JwtUtil {

    /**
     * 生成 JWT
     *
     * @param secretKey 签名密钥
     * @param ttlMillis 有效期（毫秒）
     * @param claims    自定义载荷
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .setExpiration(exp)
                .compact();
    }

    /**
     * 解析 JWT，失败会抛出异常（签名错误/过期等）
     */
    public static Claims parseJWT(String secretKey, String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}
