package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 订单统计报表 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderReportVO implements Serializable {

    /** 日期列表，以逗号分隔 */
    private String dateList;

    /** 每日订单总数列表，以逗号分隔 */
    private String orderCountList;

    /** 每日有效订单数列表，以逗号分隔 */
    private String validOrderCountList;

    /** 区间订单总数 */
    private Integer totalOrderCount;

    /** 区间有效订单数 */
    private Integer validOrderCount;

    /** 订单完成率 */
    private Double orderCompletionRate;
}
