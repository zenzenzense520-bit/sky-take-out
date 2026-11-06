package com.sky.service;

import com.sky.dto.OrderDTO;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderVO;

/**
 * 订单业务接口（管理端订单流转 + 用户端下单查询）
 */
public interface OrderService {

    /** 用户端：提交订单，返回订单视图对象 */
    OrderVO submit(OrderDTO orderDTO);

    /** 用户端：历史订单分页查询 */
    PageResult pageQuery(int page, int pageSize, Integer status);

    /** 用户端：取消订单 */
    void userCancelById(Long id);

    /** 用户端：催单 */
    void reminder(Long id);

    /** 管理端：条件分页查询订单 */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /** 管理端：查询订单详情（含明细） */
    OrderVO getOrderDetail(Long id);

    /** 管理端：接单 */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /** 管理端：拒单 */
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    /** 管理端：取消订单 */
    void cancel(OrdersCancelDTO ordersCancelDTO);

    /** 管理端：派送订单 */
    void delivery(Long id);

    /** 管理端：完成订单 */
    void complete(Long id);
}
