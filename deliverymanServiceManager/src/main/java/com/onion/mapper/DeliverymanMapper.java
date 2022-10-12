package com.onion.mapper;

import com.onion.entity.pojo.Deliveryman;
import com.onion.enummeration.DeliverymanStatus;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author 4U
* @description 针对表【deliveryman】的数据库操作Mapper
* @createDate 2022-10-11 17:26:55
* @Entity com.onion.entity.pojo.Deliveryman
*/
@Mapper
@Repository
public interface DeliverymanMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Deliveryman record);

    int insertSelective(Deliveryman record);

    Deliveryman selectByPrimaryKey(Integer id);

    List<Deliveryman> selectByStatus(DeliverymanStatus deliverymanStatus);

    int updateByPrimaryKeySelective(Deliveryman record);

    int updateByPrimaryKey(Deliveryman record);


}
