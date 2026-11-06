package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 菜品业务实现（骨架，方法逻辑待迭代补充）
 */
@Service
@Slf4j
public class DishServiceImpl implements DishService {

    private final DishMapper dishMapper;
    private final DishFlavorMapper dishFlavorMapper;

    public DishServiceImpl(DishMapper dishMapper, DishFlavorMapper dishFlavorMapper) {
        this.dishMapper = dishMapper;
        this.dishFlavorMapper = dishFlavorMapper;
    }

    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        // TODO 迭代二：保存菜品主体后批量保存口味，使用 @Transactional 保证一致性
        log.info("【骨架】新增菜品及口味: {}", dishDTO);
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        // TODO 迭代二：PageHelper 分页并补充分类名称
        log.info("【骨架】菜品分页查询: {}", dishPageQueryDTO);
        return new PageResult(0, Collections.emptyList());
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        // TODO 迭代二：校验起售状态与套餐引用后删除菜品及口味
        log.info("【骨架】批量删除菜品: {}", ids);
    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        // TODO 迭代二：查询菜品主体并组装口味与分类名
        log.info("【骨架】查询菜品详情: id={}", id);
        return null;
    }

    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        // TODO 迭代二：更新菜品，先删旧口味再插新口味
        log.info("【骨架】修改菜品及口味: {}", dishDTO);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        // TODO 迭代二：更新菜品状态，停售时同步清理缓存
        log.info("【骨架】菜品起售停售: status={}, id={}", status, id);
    }

    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        // TODO 迭代二：按分类查询起售菜品并组装口味
        log.info("【骨架】查询菜品列表: {}", dish);
        return Collections.emptyList();
    }
}
