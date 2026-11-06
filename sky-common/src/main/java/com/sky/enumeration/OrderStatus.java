package com.sky.enumeration;

/**
 * 订单状态枚举，覆盖订单完整生命周期
 */
public enum OrderStatus {

    /** 待付款 */
    PENDING_PAYMENT(1),

    /** 待接单 */
    PENDING_ACCEPT(2),

    /** 已接单 */
    ACCEPTED(3),

    /** 派送中 */
    DELIVERY_IN_PROGRESS(4),

    /** 已完成 */
    COMPLETED(5),

    /** 已取消 */
    CANCELLED(6),

    /** 退款 */
    REFUND(7);

    private final Integer value;

    OrderStatus(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
