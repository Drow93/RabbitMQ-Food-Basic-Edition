package com.onion.service;

import com.onion.entity.vo.OrderCreateVO;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author onion_Knight
 */
public interface OrderService {
    void createOrder(OrderCreateVO orderCreateVO) throws IOException, TimeoutException;
}
