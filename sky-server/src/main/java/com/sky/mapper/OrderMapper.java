package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 订单 Mapper（SQL 待迭代补充）
 */
@Mapper
public interface OrderMapper {

    /** 新增订单 */
    @AutoFill(OperationType.INSERT)
    void insert(Orders orders);

    /** 条件分页查询订单 */
    List<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /** 根据 id 查询订单 */
    Orders getById(Long id);

    /** 根据订单号查询订单 */
    Orders getByNumber(String number);

    /** 修改订单 */
    @AutoFill(OperationType.UPDATE)
    void update(Orders orders);
}
