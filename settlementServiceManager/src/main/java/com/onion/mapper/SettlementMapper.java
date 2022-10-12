package com.onion.mapper;

import com.onion.entity.pojo.Settlement;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author 4U
* @description 针对表【settlement】的数据库操作Mapper
* @createDate 2022-10-12 10:38:30
* @Entity com.onion.entity.pojo.Settlement
*/
@Mapper
@Repository
public interface SettlementMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Settlement record);

    int insertSelective(Settlement record);

    Settlement selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Settlement record);

    int updateByPrimaryKey(Settlement record);

}
