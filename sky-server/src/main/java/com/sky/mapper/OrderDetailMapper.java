package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 订单明细 Mapper（SQL 待迭代补充）
 */
@Mapper
public interface OrderDetailMapper {

    /** 批量插入订单明细 */
    void insertBatch(List<OrderDetail> orderDetails);

    /** 根据订单 id 查询明细 */
    List<OrderDetail> getByOrderId(Long orderId);
}
