package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 员工登录请求参数
 */
@Data
public class EmployeeLoginDTO implements Serializable {

    private String username;
    private String password;
}
