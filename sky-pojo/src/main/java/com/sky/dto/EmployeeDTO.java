package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 员工新增/编辑请求参数
 */
@Data
public class EmployeeDTO implements Serializable {

    private Long id;
    private String name;
    private String username;
    private String phone;
    private String sex;
    private String idNumber;
}
