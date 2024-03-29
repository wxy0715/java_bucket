package com.wxy.seata.controller;

import com.wxy.seata.entity.User;
import com.wxy.seata.mapper.UserMapper;
import com.wxy.seata.service.api.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wxy
 * @since 2023-01-06
 */
@RestController
@RequestMapping("/")
public class UserTestSeataController {
    private static final Logger log = LoggerFactory.getLogger(Class.class);

    @Resource
    private IUserService userService;
    @Resource
    private UserMapper userMapper;

    @PostMapping("/save")
    public void save(){
        userService.save();
    }

    @PostMapping("/save1")
    public void save1(){
        userService.save1();
    }

    @GetMapping("/test")
    public List<User> test(){
        return userMapper.selectList(null);
    }
}
