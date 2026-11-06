package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

/**
 * 数据统计报表业务接口
 */
public interface ReportService {

    /** 营业额统计 */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /** 用户统计 */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /** 订单统计 */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    /** 销量排名 Top10 统计 */
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);

    /** 导出运营数据 Excel 报表 */
    void exportBusinessData(HttpServletResponse response);
}
