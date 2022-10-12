package com.onion.entity.pojo;

import java.io.Serializable;
import java.util.Date;

import com.onion.enummeration.RestaurantStatus;
import lombok.Data;

/**
 * 
 * @TableName restaurant
 */
@Data
public class Restaurant implements Serializable {
    /**
     * 餐厅id
     */
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 地址
     */
    private String address;

    /**
     * 状态
     */
    private RestaurantStatus status;

    /**
     * 结算id
     */
    private Integer settlementId;

    /**
     * 时间
     */
    private Date date;

    private static final long serialVersionUID = 1L;
}