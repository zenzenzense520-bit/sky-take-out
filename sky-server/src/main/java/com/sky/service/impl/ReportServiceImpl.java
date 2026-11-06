package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据统计报表业务实现
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final OrderDetailMapper orderDetailMapper;

    public ReportServiceImpl(OrderMapper orderMapper,
                             UserMapper userMapper,
                             OrderDetailMapper orderDetailMapper) {
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
        this.orderDetailMapper = orderDetailMapper;
    }

    /**
     * 生成从 begin 到 end 范围内的日期集合
     */
    private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate cur = begin;
        while (!cur.isAfter(end)) {
            dateList.add(cur);
            cur = cur.plusDays(1);
        }
        return dateList;
    }

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Double> turnoverList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map<String, Object> map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);

            Double turnover = orderMapper.sumByMap(map);
            turnoverList.add(turnover != null ? turnover : 0.0);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 总用户量：截至当天 23:59:59 注册的所有用户
            Map<String, Object> totalMap = new HashMap<>();
            totalMap.put("end", endTime);
            Integer totalUser = userMapper.countByMap(totalMap);
            totalUserList.add(totalUser != null ? totalUser : 0);

            // 新增用户量：当天注册的用户
            Map<String, Object> newMap = new HashMap<>();
            newMap.put("begin", beginTime);
            newMap.put("end", endTime);
            Integer newUser = userMapper.countByMap(newMap);
            newUserList.add(newUser != null ? newUser : 0);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 每天订单总数
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("begin", beginTime);
            orderMap.put("end", endTime);
            Integer orderCount = orderMapper.countByMap(orderMap);
            orderCountList.add(orderCount != null ? orderCount : 0);

            // 每天已完成的有效订单数
            orderMap.put("status", Orders.COMPLETED);
            Integer validOrderCount = orderMapper.countByMap(orderMap);
            validOrderCountList.add(validOrderCount != null ? validOrderCount : 0);
        }

        int totalOrderCount = orderCountList.stream().mapToInt(Integer::intValue).sum();
        int validOrderCount = validOrderCountList.stream().mapToInt(Integer::intValue).sum();
        double orderCompletionRate = totalOrderCount == 0 ? 0.0 : (double) validOrderCount / totalOrderCount;

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> list = orderDetailMapper.getSalesTop10(beginTime, endTime);
        if (list == null) {
            list = new ArrayList<>();
        }

        List<String> nameList = list.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numberList = list.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
    }

    @Override
    public void exportBusinessData(HttpServletResponse response) {
        // 统计近 30 天的运营数据
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        List<LocalDate> dateList = getDateList(begin, end);

        // 1. 创建 Excel 工作簿
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("运营数据报表");

            // 设置表头
            XSSFRow row0 = sheet.createRow(0);
            row0.createCell(0).setCellValue("苍穹外卖运营数据统计（近30天）");

            XSSFRow row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("统计区间：" + begin + " 至 " + end);

            // 明细表头
            XSSFRow row3 = sheet.createRow(3);
            row3.createCell(0).setCellValue("日期");
            row3.createCell(1).setCellValue("营业额(元)");
            row3.createCell(2).setCellValue("有效订单数");
            row3.createCell(3).setCellValue("订单总数");
            row3.createCell(4).setCellValue("完成率");
            row3.createCell(5).setCellValue("新增用户数");

            int rowIndex = 4;
            for (LocalDate date : dateList) {
                LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
                LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

                // 营业额
                Map<String, Object> map = new HashMap<>();
                map.put("begin", beginTime);
                map.put("end", endTime);
                map.put("status", Orders.COMPLETED);
                Double turnover = orderMapper.sumByMap(map);
                turnover = turnover != null ? turnover : 0.0;

                // 有效订单数
                Integer validOrderCount = orderMapper.countByMap(map);
                validOrderCount = validOrderCount != null ? validOrderCount : 0;

                // 订单总数
                map.remove("status");
                Integer orderCount = orderMapper.countByMap(map);
                orderCount = orderCount != null ? orderCount : 0;

                // 新增用户
                Integer newUser = userMapper.countByMap(map);
                newUser = newUser != null ? newUser : 0;

                double completionRate = orderCount == 0 ? 0.0 : (double) validOrderCount / orderCount;

                XSSFRow row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(date.toString());
                row.createCell(1).setCellValue(turnover);
                row.createCell(2).setCellValue(validOrderCount);
                row.createCell(3).setCellValue(orderCount);
                row.createCell(4).setCellValue(String.format("%.2f%%", completionRate * 100));
                row.createCell(5).setCellValue(newUser);
            }

            // 写入响应流
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=Operating_Report.xlsx");
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
        } catch (IOException e) {
            log.error("导出运营数据报表异常", e);
        }
    }
}
