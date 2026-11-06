package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 菜品口味 Mapper（SQL 待迭代补充）
 */
@Mapper
public interface DishFlavorMapper {

    /** 批量插入口味 */
    void insertBatch(List<DishFlavor> flavors);

    /** 根据菜品 id 删除口味 */
    void deleteByDishId(Long dishId);

    /** 根据菜品 id 查询口味列表 */
    List<DishFlavor> getByDishId(Long dishId);
}
