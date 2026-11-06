package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置属性，前缀 sky.wechat
 */
@Component
@ConfigurationProperties(prefix = "sky.wechat")
@Data
public class WeChatProperties {

    /** 小程序 appid */
    private String appid;

    /** 小程序密钥 */
    private String secret;

    /** 登录凭证校验接口地址 */
    private String jscode2sessionUrl;
}
