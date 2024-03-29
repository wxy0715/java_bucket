package com.wxy.mybatisplus.controller;

import com.wxy.mybatisplus.entity.User;
import com.wxy.mybatisplus.service.api.IUserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wxy
 * @since 2023-01-06
 */
@RestController
public class UserController {

    @Resource
    private IUserService userService;

    @RequestMapping("/test")
    public User getNameById(){
        return userService.getById(1612349053121654786L);
    }

    @RequestMapping("/get")
    public User get(){
        return userService.get();
    }

    @RequestMapping("/get1")
    public void get1(){
        userService.get1();
    }

    @RequestMapping("/get2")
    public void get2(){
        userService.get2();
    }

    @RequestMapping("/get3")
    public void get3(){
        userService.get3();
    }
}
