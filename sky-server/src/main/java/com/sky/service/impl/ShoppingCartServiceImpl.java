package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车业务实现
 */
@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartMapper shoppingCartMapper;
    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;

    public ShoppingCartServiceImpl(ShoppingCartMapper shoppingCartMapper,
                                   DishMapper dishMapper,
                                   SetmealMapper setmealMapper) {
        this.shoppingCartMapper = shoppingCartMapper;
        this.dishMapper = dishMapper;
        this.setmealMapper = setmealMapper;
    }

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(userId);

        // 1. 判断当前加入购物车的商品是否已经存在
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list != null && !list.isEmpty()) {
            // 如果已存在，数量 +1
            ShoppingCart cartItem = list.get(0);
            cartItem.setNumber(cartItem.getNumber() + 1);
            shoppingCartMapper.updateNumber(cartItem);
        } else {
            // 如果不存在，查询菜品或套餐详情后插入新记录
            if (shoppingCartDTO.getDishId() != null) {
                Dish dish = dishMapper.getById(shoppingCartDTO.getDishId());
                if (dish != null) {
                    shoppingCart.setName(dish.getName());
                    shoppingCart.setImage(dish.getImage());
                    shoppingCart.setAmount(dish.getPrice());
                }
            } else if (shoppingCartDTO.getSetmealId() != null) {
                Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
                if (setmeal != null) {
                    shoppingCart.setName(setmeal.getName());
                    shoppingCart.setImage(setmeal.getImage());
                    shoppingCart.setAmount(setmeal.getPrice());
                }
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart query = ShoppingCart.builder().userId(userId).build();
        return shoppingCartMapper.list(query);
    }

    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }

    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart query = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, query);
        query.setUserId(userId);

        List<ShoppingCart> list = shoppingCartMapper.list(query);
        if (list != null && !list.isEmpty()) {
            ShoppingCart cartItem = list.get(0);
            if (cartItem.getNumber() > 1) {
                cartItem.setNumber(cartItem.getNumber() - 1);
                shoppingCartMapper.updateNumber(cartItem);
            } else {
                shoppingCartMapper.delete(cartItem);
            }
        }
    }
}
