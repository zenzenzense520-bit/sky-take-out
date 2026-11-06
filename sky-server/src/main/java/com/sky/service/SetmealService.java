package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

/**
 * 套餐业务接口
 */
public interface SetmealService {

    /** 新增套餐，同时保存套餐菜品关联 */
    void saveWithDish(SetmealDTO setmealDTO);

    /** 套餐分页查询 */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /** 批量删除套餐 */
    void deleteBatch(List<Long> ids);

    /** 根据 id 查询套餐及关联菜品 */
    SetmealVO getByIdWithDish(Long id);

    /** 修改套餐及关联菜品 */
    void updateWithDish(SetmealDTO setmealDTO);

    /** 套餐起售/停售 */
    void startOrStop(Integer status, Long id);

    /** 条件查询（用户端） */
    List<Setmeal> list(Setmeal setmeal);
}
