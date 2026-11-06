package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户统计报表 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReportVO implements Serializable {

    /** 日期列表，以逗号分隔 */
    private String dateList;

    /** 截至当日的总用户量列表，以逗号分隔 */
    private String totalUserList;

    /** 当日新增用户量列表，以逗号分隔 */
    private String newUserList;
}
