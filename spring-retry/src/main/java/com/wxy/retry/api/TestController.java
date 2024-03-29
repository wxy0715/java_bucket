package com.wxy.retry.api;

import com.wxy.retry.service.IBusinessService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("")
public class TestController {

    @Resource
    private IBusinessService businessService;

    @GetMapping("test")
    @SneakyThrows(Exception.class)
    public String retryable(@RequestParam(required = false) String msg) {
        return this.businessService.retry(msg);
    }

}
