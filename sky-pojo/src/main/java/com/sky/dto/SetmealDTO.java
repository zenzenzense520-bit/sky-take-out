package com.sky.dto;

import com.sky.entity.SetmealDish;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 套餐新增/编辑请求参数，携带套餐内菜品列表
 */
@Data
public class SetmealDTO implements Serializable {

    private Long id;
    private Long categoryId;
    private String name;
    private BigDecimal price;
    private String description;
    private String image;
    private Integer status;
    private List<SetmealDish> setmealDishes = new ArrayList<>();
}
