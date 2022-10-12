package com.onion.mapper;


import com.onion.entity.pojo.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author 4U
* @description 针对表【order_detail】的数据库操作Mapper
* @createDate 2022-10-11 11:26:40
* @Entity generator.domain.OrderDetail
*/
@Mapper
@Repository
public interface OrderDetailMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(OrderDetail record);

    int insertSelective(OrderDetail record);

    OrderDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderDetail record);

    int updateByPrimaryKey(OrderDetail record);

}
