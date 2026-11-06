package com.sky.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 套餐实体，对应 setmeal 表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Setmeal implements Serializable {

    private Long id;
    private Long categoryId;
    private String name;
    private BigDecimal price;
    private Integer status;
    private String description;
    private String image;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private Long createUser;
    private Long updateUser;
}
