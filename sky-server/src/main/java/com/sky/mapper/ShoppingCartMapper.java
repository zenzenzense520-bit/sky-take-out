package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购物车 Mapper
 */
@Mapper
public interface ShoppingCartMapper {

    /** 新增购物车条目 */
    void insert(ShoppingCart shoppingCart);

    /** 查询购物车列表 */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /** 修改商品数量 */
    void updateNumber(ShoppingCart shoppingCart);

    /** 删除单个购物车条目 */
    void delete(ShoppingCart shoppingCart);

    /** 清空当前用户购物车 */
    void deleteByUserId(Long userId);

    /** 批量插入购物车（再来一单功能使用） */
    void insertBatch(@Param("shoppingCartList") List<ShoppingCart> shoppingCartList);
}
