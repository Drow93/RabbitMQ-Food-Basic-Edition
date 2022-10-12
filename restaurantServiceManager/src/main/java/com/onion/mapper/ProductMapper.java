package com.onion.mapper;

import com.onion.entity.pojo.Product;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author 4U
* @description 针对表【product】的数据库操作Mapper
* @createDate 2022-10-11 16:21:22
* @Entity com.onion.entity.pojo.Product
*/
@Mapper
@Repository
public interface ProductMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

}
