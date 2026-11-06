package com.sky.vo;

import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 订单视图对象：订单主体 + 明细列表
 */
@Data
@NoArgsConstructor
public class OrderVO extends Orders implements Serializable {

    /** 订单明细 */
    private List<OrderDetail> orderDetailList;
}
