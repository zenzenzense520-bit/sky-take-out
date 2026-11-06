package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

/**
 * 菜品业务接口
 */
public interface DishService {

    /** 新增菜品，同时保存口味列表 */
    void saveWithFlavor(DishDTO dishDTO);

    /** 菜品分页查询 */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /** 批量删除菜品 */
    void deleteBatch(List<Long> ids);

    /** 根据 id 查询菜品及口味 */
    DishVO getByIdWithFlavor(Long id);

    /** 修改菜品及口味 */
    void updateWithFlavor(DishDTO dishDTO);

    /** 菜品起售/停售 */
    void startOrStop(Integer status, Long id);

    /** 条件查询菜品及口味（用户端） */
    List<DishVO> listWithFlavor(Dish dish);

    /** 根据分类 id 查询菜品列表（管理端） */
    List<Dish> list(Long categoryId);
}
