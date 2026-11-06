package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 菜品 Mapper（SQL 待迭代补充）
 */
@Mapper
public interface DishMapper {

    /** 新增菜品 */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    /** 分页查询菜品 */
    List<Dish> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /** 批量删除菜品 */
    void deleteBatch(List<Long> ids);

    /** 根据 id 查询菜品 */
    Dish getById(Long id);

    /** 修改菜品 */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /** 动态条件查询菜品列表 */
    List<Dish> list(Dish dish);
}
