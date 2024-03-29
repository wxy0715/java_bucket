package com.wxy.seata1.service.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxy.seata1.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wxy
 * @since 2023-01-06
 */

public interface IUserService extends IService<User> {


    void save();
}
