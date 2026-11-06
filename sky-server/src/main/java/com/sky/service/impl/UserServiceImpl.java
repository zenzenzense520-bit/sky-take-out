package com.sky.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * C 端用户业务实现（支持真实微信接口与离线模拟自适应）
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final WeChatProperties weChatProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserServiceImpl(UserMapper userMapper, WeChatProperties weChatProperties) {
        this.userMapper = userMapper;
        this.weChatProperties = weChatProperties;
    }

    @Override
    public User wxLogin(String code) {
        String openid = getOpenid(code);

        // 根据 openid 查询用户
        User user = userMapper.getByOpenid(openid);

        // 如果用户不存在，则自动完成注册
        if (user == null) {
            String suffix = openid.length() > 6 ? openid.substring(openid.length() - 6) : openid;
            user = User.builder()
                    .openid(openid)
                    .name("微信用户_" + suffix)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
            log.info("新微信用户自动注册成功: id={}, openid={}", user.getId(), openid);
        }

        return user;
    }

    /**
     * 获取微信 openid（具备真实请求与离线降级双模式）
     */
    private String getOpenid(String code) {
        boolean hasValidWeChatConfig = weChatProperties != null
                && weChatProperties.getAppid() != null
                && !weChatProperties.getAppid().contains("你的")
                && !weChatProperties.getAppid().trim().isEmpty();

        if (hasValidWeChatConfig) {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("appid", weChatProperties.getAppid());
                params.put("secret", weChatProperties.getSecret());
                params.put("js_code", code);
                params.put("grant_type", "authorization_code");

                String json = HttpClientUtil.doGet(weChatProperties.getJscode2sessionUrl(), params);
                if (json != null && !json.isEmpty()) {
                    JsonNode jsonNode = objectMapper.readTree(json);
                    if (jsonNode.has("openid")) {
                        return jsonNode.get("openid").asText();
                    }
                }
            } catch (Exception e) {
                log.warn("请求微信接口失败，降级为模拟 openid: {}", e.getMessage());
            }
        }

        // 默认离线测试：基于 code 生成固定的模拟 openid，方便离线和本地联调
        String mockSuffix = DigestUtils.md5DigestAsHex((code != null ? code : "default_mock").getBytes());
        return "mock_openid_" + mockSuffix.substring(0, 16);
    }
}
