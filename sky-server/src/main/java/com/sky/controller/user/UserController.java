package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户端用户接口（微信登录）
 */
@RestController
@RequestMapping("/user/user")
@Api(tags = "C 端用户相关接口")
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtProperties jwtProperties;

    public UserController(UserService userService, JwtProperties jwtProperties) {
        this.userService = userService;
        this.jwtProperties = jwtProperties;
    }

    @PostMapping("/login")
    @ApiOperation("微信登录")
    public Result<UserLoginVO> login(@RequestBody Map<String, String> loginMap) {
        // 前端传入小程序 wx.login 得到的 code
        String code = loginMap.get("code");
        User user = userService.wxLogin(code);

        // TODO 迭代三：骨架阶段 user 可能为 null，补齐微信登录后即可签发令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user == null ? null : user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user == null ? null : user.getId())
                .openid(user == null ? null : user.getOpenid())
                .token(token)
                .build();
        return Result.success(userLoginVO);
    }
}
