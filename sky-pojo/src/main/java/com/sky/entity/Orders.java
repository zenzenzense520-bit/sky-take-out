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
