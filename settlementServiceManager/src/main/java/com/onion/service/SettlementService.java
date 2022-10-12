package com.onion.service;

import java.math.BigDecimal;

/**
 * @author onion_Knight
 */
public interface SettlementService {
    Integer settlement(Integer accountId, BigDecimal amoount);
}
