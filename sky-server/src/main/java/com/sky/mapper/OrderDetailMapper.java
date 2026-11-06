package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单明细 Mapper
 */
@Mapper
public interface OrderDetailMapper {

    /** 批量插入订单明细 */
    void insertBatch(@Param("orderDetails") List<OrderDetail> orderDetails);

    /** 根据订单 id 查询明细 */
    List<OrderDetail> getByOrderId(Long orderId);

    /** 查询指定时间范围内销量排名前 10 的商品（报表统计使用） */
    List<GoodsSalesDTO> getSalesTop10(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);
}
