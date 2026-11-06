package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 分类 Mapper（SQL 待迭代补充，可采用 XML 或注解方式）
 */
@Mapper
public interface CategoryMapper {

    /** 新增分类 */
    @AutoFill(OperationType.INSERT)
    void insert(Category category);

    /** 分页查询 */
    List<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /** 根据 id 删除 */
    void deleteById(Long id);

    /** 修改分类 */
    @AutoFill(OperationType.UPDATE)
    void update(Category category);

    /** 条件查询分类列表 */
    List<Category> list(Category category);

    /** 根据 id 查询 */
    Category getById(Long id);
}
