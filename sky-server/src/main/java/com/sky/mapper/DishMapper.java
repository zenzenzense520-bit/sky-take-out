package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜品 Mapper
 */
@Mapper
public interface DishMapper {

    /** 新增菜品 */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    /** 分页查询菜品（含分类名称） */
    List<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /** 批量删除菜品 */
    void deleteBatch(@Param("ids") List<Long> ids);

    /** 根据 id 查询菜品 */
    Dish getById(Long id);

    /** 修改菜品 */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /** 动态条件查询菜品列表 */
    List<Dish> list(Dish dish);

    /** 统计某分类下的菜品数量，删除分类前校验 */
    Integer countByCategoryId(Long categoryId);

    /** 根据套餐 id 查询关联的所有菜品 */
    List<Dish> getBySetmealId(Long setmealId);
}
