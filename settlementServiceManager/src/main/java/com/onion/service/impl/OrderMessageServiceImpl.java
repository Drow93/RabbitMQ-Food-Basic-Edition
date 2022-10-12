package com.onion.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onion.entity.dto.OrderMessageDTO;
import com.onion.entity.pojo.Settlement;
import com.onion.enummeration.OrderStatus;
import com.onion.enummeration.SettlementStatus;
import com.onion.mapper.SettlementMapper;
import com.onion.service.OrderMessageService;
import com.onion.service.SettlementService;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
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
    private SettlementService settlementService;

    @Autowired
    private SettlementMapper settlementMapper;

    ObjectMapper objectMapper = new ObjectMapper();

    @Async
    @Override
    public void handleMessage() throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);

        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(
                    "exchange.order.settlement",
                    BuiltinExchangeType.FANOUT,
                    true,
                    false,
                    null);

            channel.queueDeclare(
                    "queue.settlement",
                    true,
                    false,
                    false,
                    null);

            channel.queueBind(
                    "queue.settlement",
                    "exchange.order.settlement",
                    "key.settlement"
            );

            channel.basicConsume(
                    "queue.settlement",
                    true,
                    deliverCallback,
                    consumerTag -> {});

            while (true) {
                Thread.sleep(99999999);
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
            Settlement settlement = new Settlement();
            settlement.setAmount(orderMessageDTO.getPrice());
            settlement.setDate(new Date());
            settlement.setOrderId(orderMessageDTO.getOrderId());
            Integer transactionId = settlementService.settlement(orderMessageDTO.getAccountId(), orderMessageDTO.getPrice());
            settlement.setStatus(SettlementStatus.SUCCESS);
            settlement.setTransactionId(transactionId);
            settlementMapper.insertSelective(settlement);
            orderMessageDTO.setSettlementId(settlement.getId());
            log.info("settlementOrderDTO:{}", orderMessageDTO);
            try(Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {
                String msg = objectMapper.writeValueAsString(orderMessageDTO);
                channel.basicPublish(
                        "exchange.settlement.order",
                        "key.order",
                        null,
                        msg.getBytes());
            }
        } catch (Exception e) {
            log.info(e.getMessage(),e);
        }
    });
}
