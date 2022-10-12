package com.onion.mapper;

import com.onion.entity.pojo.Restaurant;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author 4U
* @description 针对表【restaurant】的数据库操作Mapper
* @createDate 2022-10-11 16:21:22
* @Entity com.onion.entity.pojo.Restaurant
*/
@Mapper
@Repository
public interface RestaurantMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Restaurant record);

    int insertSelective(Restaurant record);

    Restaurant selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Restaurant record);

    int updateByPrimaryKey(Restaurant record);

}
