package com.sky.service.impl;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 套餐业务实现（骨架，方法逻辑待迭代补充）
 */
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    private final SetmealMapper setmealMapper;
    private final SetmealDishMapper setmealDishMapper;

    public SetmealServiceImpl(SetmealMapper setmealMapper, SetmealDishMapper setmealDishMapper) {
        this.setmealMapper = setmealMapper;
        this.setmealDishMapper = setmealDishMapper;
    }

    @Override
    public void saveWithDish(SetmealDTO setmealDTO) {
        // TODO 迭代二：保存套餐主体后批量保存关联菜品
        log.info("【骨架】新增套餐及关联菜品: {}", setmealDTO);
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        // TODO 迭代二：PageHelper 分页并补充分类名称
        log.info("【骨架】套餐分页查询: {}", setmealPageQueryDTO);
        return new PageResult(0, Collections.emptyList());
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        // TODO 迭代二：校验起售状态后删除套餐及关联数据
        log.info("【骨架】批量删除套餐: {}", ids);
    }

    @Override
    public SetmealVO getByIdWithDish(Long id) {
        // TODO 迭代二：查询套餐主体并组装关联菜品
        log.info("【骨架】查询套餐详情: id={}", id);
        return null;
    }

    @Override
    public void updateWithDish(SetmealDTO setmealDTO) {
        // TODO 迭代二：更新套餐，先删旧关联再插新关联
        log.info("【骨架】修改套餐及关联菜品: {}", setmealDTO);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        // TODO 迭代二：更新套餐状态并清理缓存
        log.info("【骨架】套餐起售停售: status={}, id={}", status, id);
    }
}
