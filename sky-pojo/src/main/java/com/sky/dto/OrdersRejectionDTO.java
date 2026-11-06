package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 拒单请求参数
 */
@Data
public class OrdersRejectionDTO implements Serializable {

    private Long id;
    private String rejectionReason;
}
