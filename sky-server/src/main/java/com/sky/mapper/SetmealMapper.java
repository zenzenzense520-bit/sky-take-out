package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 套餐 Mapper（SQL 待迭代补充）
 */
@Mapper
public interface SetmealMapper {

    /** 新增套餐 */
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    /** 分页查询套餐 */
    List<Setmeal> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /** 批量删除套餐 */
    void deleteBatch(List<Long> ids);

    /** 根据 id 查询套餐 */
    Setmeal getById(Long id);

    /** 修改套餐 */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    /** 动态条件查询套餐列表 */
    List<Setmeal> list(Setmeal setmeal);
}
