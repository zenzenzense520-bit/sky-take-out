package com.sky.service.impl;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 购物车业务实现（骨架，方法逻辑待迭代补充）
 */
@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartMapper shoppingCartMapper;

    public ShoppingCartServiceImpl(ShoppingCartMapper shoppingCartMapper) {
        this.shoppingCartMapper = shoppingCartMapper;
    }

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        // TODO 迭代三：已存在则数量加一，不存在则新增条目
        log.info("【骨架】添加购物车: {}", shoppingCartDTO);
    }

    @Override
    public List<ShoppingCart> showShoppingCart() {
        // TODO 迭代三：按当前用户 id 查询购物车列表
        log.info("【骨架】查看购物车");
        return Collections.emptyList();
    }

    @Override
    public void cleanShoppingCart() {
        // TODO 迭代三：按当前用户 id 清空购物车
        log.info("【骨架】清空购物车");
    }

    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        // TODO 迭代三：数量大于一则减一，等于一则删除条目
        log.info("【骨架】删除购物车商品: {}", shoppingCartDTO);
    }
}
