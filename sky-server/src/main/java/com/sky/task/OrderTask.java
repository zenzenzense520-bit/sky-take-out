package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 订单定时任务
 * 1. 超时未支付订单自动取消
 * 2. 派送中订单自动完成
 */
@Component
@Slf4j
public class OrderTask {

    /**
     * 每分钟扫描一次，取消下单超过 15 分钟未支付的订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder() {
        // TODO 迭代二实现：查询待付款且下单时间超过 15 分钟的订单，更新为已取消并回滚库存
        log.info("定时任务触发：处理超时未支付订单");
    }

    /**
     * 每天凌晨 1 点处理一直处于派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder() {
        // TODO 迭代二实现：将前一日仍在派送中的订单自动置为已完成
        log.info("定时任务触发：处理派送中订单");
    }
}
