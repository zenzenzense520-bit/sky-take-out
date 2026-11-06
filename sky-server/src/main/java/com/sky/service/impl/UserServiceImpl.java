package com.sky.service.impl;

import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * C 端用户业务实现（骨架，微信登录逻辑待迭代补充）
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final WeChatProperties weChatProperties;

    public UserServiceImpl(UserMapper userMapper, WeChatProperties weChatProperties) {
        this.userMapper = userMapper;
        this.weChatProperties = weChatProperties;
    }

    @Override
    public User wxLogin(String code) {
        // TODO 迭代三：调用微信 jscode2session 接口换取 openid，不存在则自动注册
        Map<String, String> params = new HashMap<>();
        params.put("appid", weChatProperties.getAppid());
        params.put("secret", weChatProperties.getSecret());
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");
        log.info("【骨架】微信登录，临时凭证 code={}", code);
        return null;
    }
}
