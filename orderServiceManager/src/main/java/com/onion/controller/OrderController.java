package com.onion.controller;

import com.onion.entity.ResponseResult;
import com.onion.entity.vo.OrderCreateVO;
import com.onion.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author onion_Knight
 */
@Slf4j
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/orders")
    public ResponseResult createOrder(@RequestBody OrderCreateVO orderCreateVO) throws IOException, TimeoutException {
        log.info("orderCreateVo:{}",orderCreateVO);
        orderService.createOrder(orderCreateVO);
        return ResponseResult.success();
    }
}
