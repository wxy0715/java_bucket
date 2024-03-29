package com.wxy.seata1.mapper;

import com.wxy.seata1.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

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
