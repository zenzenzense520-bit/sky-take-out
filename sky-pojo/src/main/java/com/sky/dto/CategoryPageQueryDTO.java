package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 分类分页查询参数
 */
@Data
public class CategoryPageQueryDTO implements Serializable {

    private int page;
    private int pageSize;
    private String name;
    private Integer type;
}
