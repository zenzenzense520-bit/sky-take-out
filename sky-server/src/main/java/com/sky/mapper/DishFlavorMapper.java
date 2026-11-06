package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜品口味 Mapper
 */
@Mapper
public interface DishFlavorMapper {

    /** 批量插入口味 */
    void insertBatch(@Param("flavors") List<DishFlavor> flavors);

    /** 根据菜品 id 删除口味 */
    void deleteByDishId(Long dishId);

    /** 批量删除菜品口味 */
    void deleteByDishIds(@Param("dishIds") List<Long> dishIds);

    /** 根据菜品 id 查询口味列表 */
    List<DishFlavor> getByDishId(Long dishId);
}
