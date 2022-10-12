package com.onion.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onion.mapper.ProductMapper;
import com.onion.mapper.RestaurantMapper;
import com.onion.entity.dto.OrderMessageDTO;
import com.onion.entity.pojo.Product;
import com.onion.entity.pojo.Restaurant;
import com.onion.enummeration.ProductStatus;
import com.onion.enummeration.RestaurantStatus;
import com.onion.service.OrderMessageService;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author onion_Knight
 */
@Slf4j
@Service
public class OrderMessageServiceImpl implements OrderMessageService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RestaurantMapper restaurantMapper;

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
                    "exchange.order.restaurant",
                    BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null);

            channel.queueDeclare(
                    "queue.restaurant",
                    true,
                    false,
                    false,
                    null);

            channel.queueBind(
                    "queue.restaurant",
                    "exchange.order.restaurant",
                    "key.restaurant");

            channel.basicConsume(
                    "queue.restaurant",
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
            Product product = productMapper.selectByPrimaryKey(orderMessageDTO.getProductId());
            log.info("project:{}",product);
            Restaurant restaurant = restaurantMapper.selectByPrimaryKey(product.getRestaurantId());
            log.info("restaurant:{}",restaurant);
            if (ProductStatus.AVAILABLE == product.getStatus() &&
                    RestaurantStatus.OPEN == restaurant.getStatus()) {
                orderMessageDTO.setConfirmed(true);
                orderMessageDTO.setPrice(product.getPrice());
            } else {
                orderMessageDTO.setConfirmed(false);
            }
            log.info("orderMessageDTO:{}",orderMessageDTO);
            try(Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {
                String msg = objectMapper.writeValueAsString(orderMessageDTO);
                channel.basicPublish(
                        "exchange.order.restaurant",
                        "key.order",
                        null,
                        msg.getBytes());
            }
    } catch (Exception e) {
            log.info(e.getMessage(),e);
        }
    });
}
