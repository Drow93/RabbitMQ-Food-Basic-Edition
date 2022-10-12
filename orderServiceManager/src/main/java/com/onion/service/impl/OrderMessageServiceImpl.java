package com.onion.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onion.mapper.OrderDetailMapper;
import com.onion.enummeration.OrderStatus;
import com.onion.entity.dto.OrderMessageDTO;
import com.onion.entity.pojo.OrderDetail;
import com.onion.service.OrderMessageService;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author onion_Knight
 * 消息处理相关业务逻辑
 */
@Slf4j
@Service
public class OrderMessageServiceImpl implements OrderMessageService {

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Async
    public void handleMessage() throws IOException, TimeoutException, InterruptedException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);

        // Connection extends ShutdownNotifier, Closeable 因为继承了Closeable接口，所以可以通过这种方式自动关闭，不用传统的try-finally
        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {

            /*----------restaurant-------------*/
            // 声明交换机，用于order和restaurant通信的交换机
            channel.exchangeDeclare(
                    "exchange.order.restaurant", // 交换机名称
                    BuiltinExchangeType.DIRECT, // 交换机类型
                    true, // 是否开启持久化，防止重启MQ消息丢失
                    false, // 是否自动删除，即MQ中的消息没有被消费会自动删除
                    null); // 额外信息
            // 声明队列，我们只声明自己监听的队列
            channel.queueDeclare(
                    "queue.order", // 队列名称
                    true, // 是否持久化
                    false, // 队列是否是这个connection独占的
                    false, // 是否自动删除
                    null); // 额外信息
            // 绑定队列到交换机
            channel.queueBind(
                    "queue.order", // 队列名称
                    "exchange.order.restaurant", // 交换机名称
                    "key.order"); // Routing Key

            /*----------deliveryman-------------*/
            // 声明交换机
            channel.exchangeDeclare(
                    "exchange.order.deliveryman", // 交换机名称
                    BuiltinExchangeType.DIRECT, // 交换机类型
                    true, // 是否开启持久化，防止重启MQ消息丢失
                    false, // 是否自动删除，即MQ中的消息没有被消费会自动删除
                    null); // 额外信息

            // 绑定队列到交换机
            channel.queueBind(
                    "queue.order", // 队列名称
                    "exchange.order.deliveryman", // 交换机名称
                    "key.order"); // Routing Key

            /*----------settlement-------------*/
            // 注意：如果以fanout方式发布的话，双方用于发送和接收的交换机不能是同一个，因为fanout方式下，routing key是无效的，就会导致自己收到自己推送的消息
            // 声明交换机
            channel.exchangeDeclare(
                    "exchange.settlement.order",
                    BuiltinExchangeType.FANOUT,
                    true,
                    false,
                    null);
            // 绑定队列到交换机
            channel.queueBind(
                    "queue.order",
                    "exchange.settlement.order",
                    "key.order"); // 这个额Routing key值不重要，但不能没有

            // 注册消费者
            channel.basicConsume(
                    "queue.order", // 队列名
                    true, // 开启自动ACK
                    deliverCallback, // 回调方法
                    consumerTag -> {}); // 消费者标签

            /*----------reward-------------*/

            channel.exchangeDeclare(
                    "exchange.order.reward",
                    BuiltinExchangeType.TOPIC,
                    true,
                    false,
                    null);

            channel.queueBind(
                    "queue.order",
                    "exchange.order.reward",
                    "key.order"); // 降级为Dirct使用

            // 这个睡眠是为了防止线程结束，导致消费者被销毁
            while (true) {
                Thread.sleep(9999999);
            }
        }
    }

    DeliverCallback deliverCallback = ((consumerTag, delivery) ->{
        String messageBody = new String(delivery.getBody());

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);

        try {
            // 将消息反序列化为DTO
            OrderMessageDTO orderMessageDTO = objectMapper.readValue(messageBody, OrderMessageDTO.class);
            log.info("OrderMessageDTO:{}",orderMessageDTO);
            // 从数据库中获取OrderDetail
            OrderDetail orderDetail = orderDetailMapper.selectByPrimaryKey(orderMessageDTO.getOrderId());
            //根据数据库取出的订单状态决定后续操作
            switch (orderDetail.getStatus()){
                //  从数据库取出订单状态为创建中的处理方式
                case ORDER_CREATING:
                    if (orderMessageDTO.getConfirmed() && !ObjectUtils.isEmpty(orderMessageDTO.getPrice())) {
                        orderDetail.setStatus(OrderStatus.RESTAURANT_CONFIRMED);
                        orderDetail.setPrice(orderMessageDTO.getPrice());
                        orderDetailMapper.updateByPrimaryKeySelective(orderDetail);
                        try(Connection connection = factory.newConnection();
                            Channel channel = connection.createChannel()) {
                            String msg = objectMapper.writeValueAsString(orderMessageDTO);
                            channel.basicPublish(
                                    "exchange.order.deliveryman",
                                    "key.deliveryman",
                                    null,
                                    msg.getBytes());
                        }
                    } else {
                        orderDetail.setStatus(OrderStatus.FAILED);
                        orderDetailMapper.updateByPrimaryKeySelective(orderDetail);
                    }
                    break;
                case RESTAURANT_CONFIRMED:
                    if (!ObjectUtils.isEmpty(orderMessageDTO.getDeliverymanId())) {
                        orderDetail.setDeliverymanId(orderMessageDTO.getDeliverymanId());
                        orderDetail.setStatus(OrderStatus.DELIVERYMAN_CONFIRMED);
                        orderDetailMapper.updateByPrimaryKeySelective(orderDetail);
                        try(Connection connection = factory.newConnection();
                            Channel channel = connection.createChannel()) {
                            String msg = objectMapper.writeValueAsString(orderMessageDTO);
                            channel.basicPublish(
                                    "exchange.order.settlement",
                                    "key.settlement",
                                    null,
                                    msg.getBytes());
                        }
                    } else {
                        orderDetail.setStatus(OrderStatus.FAILED);
                        orderDetailMapper.updateByPrimaryKeySelective(orderDetail);
                    }
                    break;
                case DELIVERYMAN_CONFIRMED:
                    if (!ObjectUtils.isEmpty(orderMessageDTO.getSettlementId())) {
                        orderDetail.setStatus(OrderStatus.SETTLEMENT_CONFIRMED);
                        orderDetail.setSettlementId(orderMessageDTO.getSettlementId());
                        orderDetailMapper.updateByPrimaryKeySelective(orderDetail);
                        try(Connection connection = factory.newConnection();
                            Channel channel = connection.createChannel()) {
                            String msg = objectMapper.writeValueAsString(orderMessageDTO);
                            channel.basicPublish(
                                    "exchange.order.reward",
                                    "key.reward",
                                    null,
                                    msg.getBytes());
                        }
                    } else {
                        orderDetail.setStatus(OrderStatus.FAILED);
                        orderDetailMapper.updateByPrimaryKeySelective(orderDetail);
                    }
                    break;
                case SETTLEMENT_CONFIRMED:
                    if (!ObjectUtils.isEmpty(orderMessageDTO.getRewardId())) {
                        orderDetail.setStatus(OrderStatus.ORDER_CREATED);
                        orderDetail.setRewardId(orderMessageDTO.getRewardId());
                        orderDetailMapper.updateByPrimaryKeySelective(orderDetail);
                    } else {
                        orderDetail.setStatus(OrderStatus.FAILED);
                        orderDetailMapper.updateByPrimaryKeySelective(orderDetail);
                    }
                    break;

            }

        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    });


}
