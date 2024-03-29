package com.wxy.mybatisplus.service.api;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.wxy.mybatisplus.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wxy
 * @since 2023-01-06
 */

public interface IUserService extends IService<User> {

    User get();

    void get1();

    void get2();

    void get3();
}
