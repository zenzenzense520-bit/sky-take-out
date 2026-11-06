package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单定时任务
 * 1. 超时未支付订单自动取消
 * 2. 派送中订单自动完成
 */
@Component
@Slf4j
public class OrderTask {

    private final OrderMapper orderMapper;

    public OrderTask(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    /**
     * 每分钟扫描一次，取消下单超过 15 分钟仍未支付的订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder() {
        LocalDateTime time = LocalDateTime.now().minusMinutes(15);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);

        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时未支付，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
                log.info("定时任务：取消超时未支付订单 id={}", orders.getId());
            }
        }
    }

    /**
     * 每天凌晨 1 点处理前一天仍处于派送中的订单，自动修改为已完成
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder() {
        LocalDateTime time = LocalDateTime.now().minusMinutes(60);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);

        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orders.setDeliveryStatus(1);
                orderMapper.update(orders);
                log.info("定时任务：自动完成长期处于派送中的订单 id={}", orders.getId());
            }
        }
    }
}
