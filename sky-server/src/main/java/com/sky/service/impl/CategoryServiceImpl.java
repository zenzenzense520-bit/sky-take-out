package com.sky.service.impl;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 分类业务实现（骨架，方法逻辑待迭代补充）
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public void save(CategoryDTO categoryDTO) {
        // TODO 迭代二：BeanUtils 拷贝为 Category 后调用 categoryMapper.insert
        log.info("【骨架】新增分类: {}", categoryDTO);
    }

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        // TODO 迭代二：PageHelper 分页后封装 PageResult
        log.info("【骨架】分类分页查询: {}", categoryPageQueryDTO);
        return new PageResult(0, Collections.emptyList());
    }

    @Override
    public void deleteById(Long id) {
        // TODO 迭代二：删除前校验是否关联菜品/套餐，再调用 deleteById
        log.info("【骨架】删除分类: id={}", id);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        // TODO 迭代二：拷贝为 Category 后调用 update
        log.info("【骨架】修改分类: {}", categoryDTO);
    }

    @Override
    public List<Category> list(Integer type) {
        // TODO 迭代二：调用 categoryMapper.list 按类型查询
        log.info("【骨架】查询分类列表: type={}", type);
        return Collections.emptyList();
    }
}
