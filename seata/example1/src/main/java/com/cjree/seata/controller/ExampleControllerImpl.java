package com.cjree.seata.controller;

import com.cjree.core.basic.common.Response;
import com.cjree.core.common.ResponseCode;
import com.cjree.core.common.Result;
import com.cjree.seata.service.ExampleService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/seata")
public class ExampleControllerImpl {

    @Resource
    private ExampleService exampleService;

    @PostMapping("/save")
    public Result<Object> save(){
        exampleService.save();
        return Response.ok(ResponseCode.SUCCESS,1L);
    }
}
