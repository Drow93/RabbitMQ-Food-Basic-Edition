package com.onion.entity.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.onion.enummeration.RewardStatus;
import lombok.Data;

/**
 * 
 * @TableName reward
 */
@Data
public class Reward implements Serializable {
    /**
     * 奖励id
     */
    private Integer id;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 积分量
     */
    private BigDecimal amount;

    /**
     * 状态
     */
    private RewardStatus status;

    /**
     * 时间
     */
    private Date date;

    private static final long serialVersionUID = 1L;
}