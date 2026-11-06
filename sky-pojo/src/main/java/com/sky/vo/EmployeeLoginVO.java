package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 员工登录成功返回结构
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeLoginVO implements Serializable {

    private Long id;
    private String userName;
    private String name;
    private String token;
}
