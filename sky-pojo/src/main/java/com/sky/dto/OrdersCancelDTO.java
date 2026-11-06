package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 取消订单请求参数
 */
@Data
public class OrdersCancelDTO implements Serializable {

    private Long id;
    private String cancelReason;
}
