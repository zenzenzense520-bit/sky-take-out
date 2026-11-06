package com.sky.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrderDTO;
import com.sky.dto.OrderPaymentDTO;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单业务实现
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final AddressBookMapper addressBookMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    private final WebSocketServer webSocketServer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderServiceImpl(OrderMapper orderMapper,
                            OrderDetailMapper orderDetailMapper,
                            AddressBookMapper addressBookMapper,
                            ShoppingCartMapper shoppingCartMapper,
                            WebSocketServer webSocketServer) {
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.addressBookMapper = addressBookMapper;
        this.shoppingCartMapper = shoppingCartMapper;
        this.webSocketServer = webSocketServer;
    }

    @Override
    @Transactional
    public OrderVO submit(OrderDTO orderDTO) {
        Long userId = BaseContext.getCurrentId();

        // 1. 校验收货地址
        AddressBook addressBook = addressBookMapper.getById(orderDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // 2. 校验购物车数据
        ShoppingCart scQuery = ShoppingCart.builder().userId(userId).build();
        List<ShoppingCart> cartList = shoppingCartMapper.list(scQuery);
        if (cartList == null || cartList.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 3. 构建并插入订单主表记录
        Orders orders = new Orders();
        BeanUtils.copyProperties(orderDTO, orders);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        // 默认直接置为待接单状态，方便本地全流程联调
        orders.setStatus(Orders.TO_BE_CONFIRMED);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());
        orderMapper.insert(orders);

        // 4. 构建并批量插入订单明细记录
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : cartList) {
            OrderDetail detail = new OrderDetail();
            BeanUtils.copyProperties(cart, detail);
            detail.setOrderId(orders.getId());
            orderDetailList.add(detail);
        }
        orderDetailMapper.insertBatch(orderDetailList);

        // 5. 下单成功后清空购物车
        shoppingCartMapper.deleteByUserId(userId);

        // 6. 通过 WebSocket 向管理端广播来单提醒（type = 1）
        pushWebSocketNotification(1, orders.getId(), "订单号：" + orders.getNumber());

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    @Override
    public void payment(OrderPaymentDTO orderPaymentDTO) {
        Orders orders = orderMapper.getByNumber(orderPaymentDTO.getOrderNumber());
        if (orders != null) {
            orders.setStatus(Orders.TO_BE_CONFIRMED);
            orders.setCheckoutTime(LocalDateTime.now());
            orderMapper.update(orders);

            // 推送来单提醒
            pushWebSocketNotification(1, orders.getId(), "订单号：" + orders.getNumber());
        }
    }

    @Override
    public PageResult pageQuery(int page, int pageSize, Integer status) {
        OrdersPageQueryDTO queryDTO = new OrdersPageQueryDTO();
        queryDTO.setPage(page);
        queryDTO.setPageSize(pageSize);
        queryDTO.setStatus(status);
        queryDTO.setUserId(BaseContext.getCurrentId());

        PageHelper.startPage(page, pageSize);
        Page<Orders> pageOrders = (Page<Orders>) orderMapper.pageQuery(queryDTO);

        List<OrderVO> list = new ArrayList<>();
        if (pageOrders != null && pageOrders.getTotal() > 0) {
            for (Orders o : pageOrders) {
                OrderVO vo = new OrderVO();
                BeanUtils.copyProperties(o, vo);
                vo.setOrderDetailList(orderDetailMapper.getByOrderId(o.getId()));
                list.add(vo);
            }
        }
        return new PageResult(pageOrders != null ? pageOrders.getTotal() : 0, list);
    }

    @Override
    public OrderVO getOrderDetail(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            return null;
        }
        List<OrderDetail> details = orderDetailMapper.getByOrderId(id);
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(orders, vo);
        vo.setOrderDetailList(details);
        return vo;
    }

    @Override
    public void userCancelById(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 仅待付款或待接单允许用户取消
        if (orders.getStatus() > Orders.TO_BE_CONFIRMED) {
            throw new OrderBusinessException("订单已接单或配送中，无法取消");
        }
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    public void reminder(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 通过 WebSocket 向管理端广播催单提醒（type = 2）
        pushWebSocketNotification(2, id, "订单号：" + orders.getNumber());
    }

    @Override
    public void repetition(Long id) {
        List<OrderDetail> details = orderDetailMapper.getByOrderId(id);
        if (details != null && !details.isEmpty()) {
            Long userId = BaseContext.getCurrentId();
            List<ShoppingCart> cartList = new ArrayList<>();
            for (OrderDetail d : details) {
                ShoppingCart cart = new ShoppingCart();
                BeanUtils.copyProperties(d, cart, "id");
                cart.setUserId(userId);
                cart.setCreateTime(LocalDateTime.now());
                cartList.add(cart);
            }
            shoppingCartMapper.insertBatch(cartList);
        }
    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> pageOrders = (Page<Orders>) orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList<>();
        if (pageOrders != null && pageOrders.getTotal() > 0) {
            for (Orders o : pageOrders) {
                OrderVO vo = new OrderVO();
                BeanUtils.copyProperties(o, vo);
                vo.setOrderDetailList(orderDetailMapper.getByOrderId(o.getId()));
                list.add(vo);
            }
        }
        return new PageResult(pageOrders != null ? pageOrders.getTotal() : 0, list);
    }

    @Override
    public OrderStatisticsVO statistics() {
        Integer toBeConfirmed = orderMapper.countByStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countByStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countByStatus(Orders.DELIVERY_IN_PROGRESS);

        return OrderStatisticsVO.builder()
                .toBeConfirmed(toBeConfirmed != null ? toBeConfirmed : 0)
                .confirmed(confirmed != null ? confirmed : 0)
                .deliveryInProgress(deliveryInProgress != null ? deliveryInProgress : 0)
                .build();
    }

    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();
        orderMapper.update(orders);
    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = orderMapper.getById(ordersRejectionDTO.getId());
        if (orders == null || !Orders.TO_BE_CONFIRMED.equals(orders.getStatus())) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = orderMapper.getById(ordersCancelDTO.getId());
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    public void delivery(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders == null || !Orders.CONFIRMED.equals(orders.getStatus())) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }

    @Override
    public void complete(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders == null || !Orders.DELIVERY_IN_PROGRESS.equals(orders.getStatus())) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryStatus(1);
        orderMapper.update(orders);
    }

    /**
     * 统一 WebSocket 消息推送
     */
    private void pushWebSocketNotification(int type, Long orderId, String content) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("type", type);
            map.put("orderId", orderId);
            map.put("content", content);
            webSocketServer.sendMessageToAll(objectMapper.writeValueAsString(map));
        } catch (Exception e) {
            log.warn("WebSocket 推送失败（非阻断）: {}", e.getMessage());
        }
    }
}
