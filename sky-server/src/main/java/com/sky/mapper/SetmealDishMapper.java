package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 套餐-菜品关联 Mapper
 */
@Mapper
public interface SetmealDishMapper {

    /** 批量插入套餐菜品关联 */
    void insertBatch(@Param("setmealDishes") List<SetmealDish> setmealDishes);

    /** 根据套餐 id 删除关联 */
    void deleteBySetmealId(Long setmealId);

    /** 根据套餐 id 集合批量删除关联 */
    void deleteBySetmealIds(@Param("setmealIds") List<Long> setmealIds);

    /** 根据套餐 id 查询关联菜品 */
    List<SetmealDish> getBySetmealId(Long setmealId);

    /** 统计某菜品被套餐引用的数量，删除菜品前校验 */
    Integer countByDishId(Long dishId);
}
