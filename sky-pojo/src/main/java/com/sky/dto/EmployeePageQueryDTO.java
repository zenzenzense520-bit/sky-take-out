package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 员工分页查询参数
 */
@Data
public class EmployeePageQueryDTO implements Serializable {

    private String name;
    private int page;
    private int pageSize;
}
