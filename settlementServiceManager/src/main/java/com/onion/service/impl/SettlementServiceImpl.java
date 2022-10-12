package com.onion.service.impl;

import com.onion.service.SettlementService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

/**
 * @author onion_Knight
 */
@Service
public class SettlementServiceImpl implements SettlementService {

    Random random = new Random(93);

    @Override
    public Integer settlement(Integer accountId, BigDecimal amoount) {
        // ...
        // 这里省略了关于结算的实际业务，返回的是一个假的交易id
        return random.nextInt(1000000000);
    }

}
