package com.sky.utils;

import java.util.Random;

/**
 * 随机验证码生成工具（短信/登录验证码）
 */
public class ValidateCodeUtils {

    private static final Random RANDOM = new Random();

    private ValidateCodeUtils() {
    }

    /**
     * 生成指定位数的纯数字验证码
     */
    public static Integer generateValidateCode(int length) {
        int code;
        if (length < 1 || length > 9) {
            throw new IllegalArgumentException("验证码长度需在 1~9 位之间");
        }
        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length);
        code = RANDOM.nextInt(max - min) + min;
        return code;
    }

    /**
     * 生成指定长度的随机字符串验证码（数字+字母）
     */
    public static String generateValidateCode4String(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return builder.toString();
    }
}
