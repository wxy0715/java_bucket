package com.wxy.transactional.service.api;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.wxy.transactional.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.sql.SQLException;

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

    void get21();

    void get3();

    void get4();

    void test() throws SQLException;
}
