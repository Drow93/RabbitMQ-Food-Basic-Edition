package com.onion.entity.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.onion.enummeration.SettlementStatus;
import lombok.Data;

/**
 * 
 * @TableName settlement
 */
@Data
public class Settlement implements Serializable {
    /**
     * 结算id
     */
    private Integer id;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 交易id
     */
    private Integer transactionId;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 状态
     */
    private SettlementStatus status;

    /**
     * 时间
     */
    private Date date;

    private static final long serialVersionUID = 1L;
}