package com.wxy.seata1.controller;

import com.wxy.seata1.service.api.IUserService;
import com.wxy.seata.api.IUserTestController;
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
@RequestMapping("/")
public class UserTestController implements IUserTestController {

    @Resource
    private IUserService userService;

    @Override
    public void save(){
        userService.save();
    }
}
