package com.wxy.transactional.controller;

import com.wxy.transactional.entity.User;
import com.wxy.transactional.service.api.IUserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.SQLException;

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

    @RequestMapping("/get4")
    public void get4(){
        userService.get4();
    }

    @RequestMapping("/test1")
    public void test1() throws SQLException {
        userService.test();
    }
}
