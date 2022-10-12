package com.onion.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onion.entity.dto.OrderMessageDTO;
import com.onion.entity.pojo.Reward;
import com.onion.enummeration.RewardStatus;
import com.onion.mapper.RewardMapper;
import com.onion.service.OrderMessageService;
import com.rabbitmq.client.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

/**
 * @author onion_Knight
 */
@Slf4j
@Service
public class OrderMessageServiceImpl implements OrderMessageService {

    @Autowired
    private RewardMapper rewardMapper;

    ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @Async
    @Override
    public void handleMessage() throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);

        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(
                    "exchange.order.reward",
                    BuiltinExchangeType.TOPIC,
                    true,
                    false,
                    null);

            channel.queueDeclare(
                    "queue.reward",
                    true,
                    false,
                    false,
                    null);

            channel.queueBind(
                    "queue.reward",
                    "exchange.order.reward",
                    "key.reward");

            channel.basicConsume(
                    "queue.reward",
                    true,
                    deliverCallback,
                    ConsumerTag -> {});

            while (true) {
                Thread.sleep(999999999);
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
            Reward reward = new Reward();
            reward.setOrderId(orderMessageDTO.getOrderId());
            reward.setStatus(RewardStatus.SUCCESS);
            reward.setAmount(orderMessageDTO.getPrice());
            reward.setDate(new Date());
            rewardMapper.insertSelective(reward);
            orderMessageDTO.setRewardId(reward.getId());
            log.info("orderMessageDTO:{}",orderMessageDTO);
            try(Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {
                String msg = objectMapper.writeValueAsString(orderMessageDTO);
                channel.basicPublish(
                        "exchange.order.reward",
                        "key.order",
                        null,
                        msg.getBytes());
            }
        } catch (Exception e) {
            log.info(e.getMessage(),e);
        }
    });
}
