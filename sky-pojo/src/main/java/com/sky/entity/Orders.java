package com.sky.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体，对应 orders 表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Orders implements Serializable {

    /** 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款 */
    public static final Integer PENDING_PAYMENT = 1;
    public static final Integer TO_BE_CONFIRMED = 2;
    public static final Integer CONFIRMED = 3;
    public static final Integer DELIVERY_IN_PROGRESS = 4;
    public static final Integer COMPLETED = 5;
    public static final Integer CANCELLED = 6;
    public static final Integer REFUND = 7;

    /** 支付状态 0未支付 1已支付 2退款 */
    public static final Integer UN_PAID = 0;
    public static final Integer PAID = 1;
    public static final Integer REFUNDED = 2;

    private Long id;
    /** 订单号 */
    private String number;
    private Integer status;
    private Long userId;
    private Long addressBookId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkoutTime;

    /** 支付方式：1 微信 2 支付宝 */
    private Integer payMethod;
    private BigDecimal amount;
    private String remark;
    private String userName;
    private String phone;
    private String address;
    private String consignee;
    private String cancelReason;
    private String rejectionReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime estimatedDeliveryTime;

    /** 配送状态：1 立即送出 2 具体时间 */
    private Integer deliveryStatus;
    private BigDecimal packAmount;
    private Integer tablewareNumber;
    private Integer tablewareStatus;
}
