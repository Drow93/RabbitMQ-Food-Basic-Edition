package com.onion.entity.vo;

import lombok.Data;

@Data
public class OrderCreateVO {
    /**
     * 用户id
     */
    private Integer accountId;
    /**
     * 地址
     */
    private String address;
    /**
     * 产品id
     */
    private Integer productId;
}
