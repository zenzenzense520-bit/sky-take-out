package com.sky.service;

import com.sky.entity.User;

/**
 * C 端用户业务接口
 */
public interface UserService {

    /**
     * 微信登录
     *
     * @param code 小程序登录临时凭证
     * @return 登录用户实体
     */
    User wxLogin(String code);
}
