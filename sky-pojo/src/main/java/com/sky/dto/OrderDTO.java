package com.sky.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户下单请求参数
 */
@Data
public class OrderDTO implements Serializable {

    private Long addressBookId;
    private Integer payMethod;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime estimatedDeliveryTime;

    /** 配送状态：1 立即送出 2 预约时间 */
    private Integer deliveryStatus;
    private Integer tablewareNumber;
    private Integer tablewareStatus;
    private BigDecimal packAmount;
}
