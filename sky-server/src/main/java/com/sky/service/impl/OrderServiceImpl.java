package com.sky.service.impl;

import com.sky.dto.OrderDTO;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 订单业务实现（骨架，方法逻辑待迭代补充）
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final WebSocketServer webSocketServer;

    public OrderServiceImpl(OrderMapper orderMapper, OrderDetailMapper orderDetailMapper,
                            WebSocketServer webSocketServer) {
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.webSocketServer = webSocketServer;
    }

    @Override
    public OrderVO submit(OrderDTO orderDTO) {
        // TODO 迭代三：校验购物车与地址，生成订单号、计算金额，保存订单及明细并清空购物车
        log.info("【骨架】用户下单: {}", orderDTO);
        return null;
    }

    @Override
    public PageResult pageQuery(int page, int pageSize, Integer status) {
        // TODO 迭代三：按当前用户与状态分页查询历史订单
        log.info("【骨架】历史订单分页: page={}, pageSize={}, status={}", page, pageSize, status);
        return new PageResult(0, Collections.emptyList());
    }

    @Override
    public void userCancelById(Long id) {
        // TODO 迭代三：仅待付款订单可取消，更新状态并记录原因
        log.info("【骨架】用户取消订单: id={}", id);
    }

    @Override
    public void reminder(Long id) {
        // TODO 迭代三：通过 WebSocket 向管理端发送催单提醒
        log.info("【骨架】用户催单: id={}", id);
    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        // TODO 迭代三：PageHelper 分页查询订单
        log.info("【骨架】订单分页查询: {}", ordersPageQueryDTO);
        return new PageResult(0, Collections.emptyList());
    }

    @Override
    public OrderVO getOrderDetail(Long id) {
        // TODO 迭代三：查询订单主体并组装明细列表
        log.info("【骨架】查询订单详情: id={}", id);
        return null;
    }

    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        // TODO 迭代三：校验状态后更新为已接单，通过 WebSocket 推送用户
        log.info("【骨架】接单: {}", ordersConfirmDTO);
    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        // TODO 迭代三：校验状态后拒单并记录原因
        log.info("【骨架】拒单: {}", ordersRejectionDTO);
    }

    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        // TODO 迭代三：校验状态后取消并记录原因
        log.info("【骨架】取消订单: {}", ordersCancelDTO);
    }

    @Override
    public void delivery(Long id) {
        // TODO 迭代三：校验状态后更新为派送中
        log.info("【骨架】订单派送: id={}", id);
    }

    @Override
    public void complete(Long id) {
        // TODO 迭代三：校验状态后更新为已完成
        log.info("【骨架】完成订单: id={}", id);
    }
}
