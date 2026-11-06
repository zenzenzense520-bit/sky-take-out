package com.sky.vo;

import com.sky.entity.SetmealDish;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 套餐视图对象：套餐信息 + 关联菜品 + 分类名称
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetmealVO implements Serializable {

    private Long id;
    private Long categoryId;
    private String name;
    private BigDecimal price;
    private String description;
    private String image;
    private Integer status;
    private LocalDateTime updateTime;
    private String categoryName;
    private List<SetmealDish> setmealDishes = new ArrayList<>();
}
