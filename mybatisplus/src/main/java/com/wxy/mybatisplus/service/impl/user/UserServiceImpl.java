package com.wxy.mybatisplus.service.impl.user;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxy.mybatisplus.entity.User;
import com.wxy.mybatisplus.enums.AgeEnum;
import com.wxy.mybatisplus.mapper.UserMapper;
import com.wxy.mybatisplus.service.api.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wxy
 * @since 2023-01-06
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public User get() {
        IUserService userService = (IUserService)AopContext.currentProxy();
        userService.get1();
        userService.get2();
        userService.get3();
        return null;
    }

    @Override
    public void get1(){
        log.error("master{}",baseMapper.selectList(new QueryWrapper<User>().select("id")));
    }

    @DS("slave1")
    @Override
    public void get2(){
        log.error("slave1{}",baseMapper.selectList(new QueryWrapper<User>().select("id")));
    }

    @DS("slave2")
    @Override
    public void get3(){
        log.error("slave2{}",baseMapper.selectList(new QueryWrapper<User>().select("id")));
    }
}
