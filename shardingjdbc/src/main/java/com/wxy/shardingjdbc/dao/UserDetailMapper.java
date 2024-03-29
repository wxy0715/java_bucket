package com.wxy.shardingjdbc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wxy.shardingjdbc.model.User;
import com.wxy.shardingjdbc.model.UserDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wxy
 * @since 2023-01-07
 */
@Mapper
public interface UserDetailMapper extends BaseMapper<UserDetail> {

    List<User> inner();
}
