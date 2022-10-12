package com.onion.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onion.mapper.DeliverymanMapper;
import com.onion.entity.dto.OrderMessageDTO;
import com.onion.entity.pojo.Deliveryman;
import com.onion.enummeration.DeliverymanStatus;
import com.onion.service.OrderMessageService;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * @author onion_Knight
 */
@Slf4j
@Service
public class OrderMessageServiceImpl implements OrderMessageService {

    @Autowired
    private DeliverymanMapper deliverymanMapper;

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Async
    public void handleMessage() throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);

        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(
                    "exchange.order.deliveryman",
                    BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null);

            channel.queueDeclare(
                    "queue.deliveryman",
                    true,
                    false,
                    false,
                    null);

            channel.queueBind(
                    "queue.deliveryman",
                    "exchange.order.deliveryman",
                    "key.deliveryman"
            );

            channel.basicConsume(
                    "queue.deliveryman",
                    true,
                    deliverCallback,
                    consumerTag -> {});

            while (true) {
                Thread.sleep(9999999);
            }
        }
    }

    DeliverCallback deliverCallback = ((consumerTag,delivery) -> {
        String messageBody = new String(delivery.getBody());

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);

        try {
            OrderMessageDTO orderMessageDTO = objectMapper.readValue(messageBody, OrderMessageDTO.class);
            List<Deliveryman> deliverymanList = deliverymanMapper.selectByStatus(DeliverymanStatus.AVAILABLE);
            orderMessageDTO.setDeliverymanId(deliverymanList.get(0).getId());
            log.info("orderMessageDto:{}",orderMessageDTO);
            try(Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {
                String msg = objectMapper.writeValueAsString(orderMessageDTO);
                channel.basicPublish(
                        "exchange.order.deliveryman",
                        "key.order",
                        null,
                        msg.getBytes());
            }
        } catch (Exception e) {
            log.info(e.getMessage(),e);
        }
    });
}
