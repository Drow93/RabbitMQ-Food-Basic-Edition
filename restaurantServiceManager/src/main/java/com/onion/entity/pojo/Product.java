package com.onion.entity.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.onion.enummeration.ProductStatus;
import lombok.Data;

/**
 * 
 * @TableName product
 */
@Data
public class Product implements Serializable {
    /**
     * 产品id
     */
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 地址
     */
    private Integer restaurantId;

    /**
     * 状态
     */
    private ProductStatus status;

    /**
     * 时间
     */
    private Date date;

    private static final long serialVersionUID = 1L;
}