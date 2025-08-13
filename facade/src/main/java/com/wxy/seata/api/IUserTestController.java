package com.wxy.seata.api;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * seata测试
 * @author wangxingyu
 * @date 2023/04/18 16:29:09
 */
@FeignClient("user2")
public interface IUserTestController {

    @PostMapping("/save")
    void save();
}
