package com.sky.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单条件分页查询参数（管理端）
 */
@Data
public class OrdersPageQueryDTO implements Serializable {

    private int page;
    private int pageSize;
    private String number;
    private String phone;
    private Integer status;
    private Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
