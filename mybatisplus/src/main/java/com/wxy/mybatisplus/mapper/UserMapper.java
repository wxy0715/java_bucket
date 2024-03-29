package com.wxy.mybatisplus.mapper;

import com.wxy.mybatisplus.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wxy
 * @since 2023-01-06
 */

public interface UserMapper extends BaseMapper<User> {

    Integer myCount();
}
