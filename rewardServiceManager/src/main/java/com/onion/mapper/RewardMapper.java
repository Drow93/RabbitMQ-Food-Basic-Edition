package com.onion.mapper;

import com.onion.entity.pojo.Reward;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author 4U
* @description 针对表【reward】的数据库操作Mapper
* @createDate 2022-10-12 13:45:50
* @Entity com.onion.entity.pojo.Reward
*/
@Mapper
@Repository
public interface RewardMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Reward record);

    int insertSelective(Reward record);

    Reward selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Reward record);

    int updateByPrimaryKey(Reward record);

}
