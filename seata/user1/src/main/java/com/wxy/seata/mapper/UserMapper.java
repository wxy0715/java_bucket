package com.wxy.seata.mapper;

import com.wxy.seata.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wxy
 * @since 2023-01-06
 */
public interface UserMapper extends BaseMapper<User> {

    String myCount();
}
