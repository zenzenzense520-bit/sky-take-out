package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 套餐 Mapper
 */
@Mapper
public interface SetmealMapper {

    /** 新增套餐 */
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    /** 分页查询套餐（含分类名称） */
    List<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /** 批量删除套餐 */
    void deleteBatch(@Param("ids") List<Long> ids);

    /** 根据 id 查询套餐 */
    Setmeal getById(Long id);

    /** 修改套餐 */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    /** 动态条件查询套餐列表 */
    List<Setmeal> list(Setmeal setmeal);

    /** 统计某分类下的套餐数量，删除分类前校验 */
    Integer countByCategoryId(Long categoryId);
}
