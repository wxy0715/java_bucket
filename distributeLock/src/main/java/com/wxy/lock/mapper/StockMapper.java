package com.wxy.lock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wxy.lock.entity.Stock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMapper extends BaseMapper<Stock> {

    @Update("update stock set count=count-#{count} where product_code=#{code} and count>=#{count}")
    int sole(@Param("code") Integer code, @Param("count") Integer count);

    @Select("select * from stock where product_code=#{code} for update")
    List<Stock> queryStock(Integer code);

}
