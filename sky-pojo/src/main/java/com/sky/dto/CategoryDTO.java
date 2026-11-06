package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 分类新增/编辑请求参数
 */
@Data
public class CategoryDTO implements Serializable {

    private Long id;
    private Integer type;
    private String name;
    private Integer sort;
}
