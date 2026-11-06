package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单 Mapper
 */
@Mapper
public interface OrderMapper {

    /** 新增订单 */
    void insert(Orders orders);

    /** 条件分页查询订单 */
    List<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /** 根据 id 查询订单 */
    Orders getById(Long id);

    /** 根据订单号查询订单 */
    Orders getByNumber(String number);

    /** 修改订单 */
    void update(Orders orders);

    /** 根据状态和下单时间查询超时订单（定时任务使用） */
    List<Orders> getByStatusAndOrderTimeLT(@Param("status") Integer status, @Param("orderTime") LocalDateTime orderTime);

    /** 根据状态统计订单数（管理端概览使用） */
    Integer countByStatus(Integer status);

    /** 根据条件统计营业额（报表统计使用） */
    Double sumByMap(Map map);

    /** 根据条件统计订单数（报表统计使用） */
    Integer countByMap(Map map);
}
