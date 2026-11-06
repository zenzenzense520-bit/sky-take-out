package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 营业额统计报表 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TurnoverReportVO implements Serializable {

    /** 日期列表，以逗号分隔，如 2026-11-01,2026-11-02 */
    private String dateList;

    /** 营业额列表，以逗号分隔，如 260.00,215.00 */
    private String turnoverList;
}
