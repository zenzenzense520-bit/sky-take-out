package com.sky.controller.user;

import com.sky.dto.OrderDTO;
import com.sky.dto.OrderPaymentDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端订单接口
 */
@RestController
@RequestMapping("/user/order")
@Api(tags = "C 端订单接口")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderVO> submit(@RequestBody OrderDTO orderDTO) {
        return Result.success(orderService.submit(orderDTO));
    }

    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<String> payment(@RequestBody OrderPaymentDTO orderPaymentDTO) {
        orderService.payment(orderPaymentDTO);
        return Result.success();
    }

    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> orderDetail(@PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(id));
    }

    @GetMapping("/historyOrders")
    @ApiOperation("历史订单分页查询")
    public Result<PageResult> historyOrders(int page, int pageSize, Integer status) {
        return Result.success(orderService.pageQuery(page, pageSize, status));
    }

    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result<String> cancel(@PathVariable Long id) {
        orderService.userCancelById(id);
        return Result.success();
    }

    @GetMapping("/reminder/{id}")
    @ApiOperation("催单")
    public Result<String> reminder(@PathVariable Long id) {
        orderService.reminder(id);
        return Result.success();
    }

    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result<String> repetition(@PathVariable Long id) {
        orderService.repetition(id);
        return Result.success();
    }
}
