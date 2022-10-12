package com.onion.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onion.mapper.OrderDetailMapper;
import com.onion.enummeration.OrderStatus;
import com.onion.entity.dto.OrderMessageDTO;
import com.onion.entity.pojo.OrderDetail;
import com.onion.entity.vo.OrderCreateVO;
import com.onion.service.OrderService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

/**
 * @author onion_Knight
 * 处理用户关于订单的业务请求
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    ObjectMapper objectMapper = new ObjectMapper();

    public void createOrder(OrderCreateVO orderCreateVO) throws IOException, TimeoutException {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setAccountId(orderCreateVO.getAccountId());
        orderDetail.setAddress(orderCreateVO.getAddress());
        orderDetail.setProductId(orderCreateVO.getProductId());
        orderDetail.setDate(new Date());
        orderDetail.setStatus(OrderStatus.ORDER_CREATING);
        orderDetailMapper.insertSelective(orderDetail);

        OrderMessageDTO orderMessageDTO = new OrderMessageDTO();
        orderMessageDTO.setOrderId(orderDetail.getId());
        orderMessageDTO.setAccountId(orderDetail.getAccountId());
        orderMessageDTO.setProductId(orderDetail.getProductId());

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);

        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {

            String msg = objectMapper.writeValueAsString(orderMessageDTO);
            // 推送消息
            channel.basicPublish(
                    "exchange.order.restaurant", // 交换机名称
                    "key.restaurant", // Routing Key
                    null, // 特殊参数
                    msg.getBytes()); // 消息主题
        }
    }
}
