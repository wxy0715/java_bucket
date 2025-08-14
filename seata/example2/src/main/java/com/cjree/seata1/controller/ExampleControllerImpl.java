package com.cjree.seata1.controller;

import com.cjree.core.basic.common.Response;
import com.cjree.core.common.ResponseCode;
import com.cjree.core.common.Result;
import com.cjree.seata.controller.ExampleController;
import com.cjree.seata1.service.ExampleService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ExampleControllerImpl implements ExampleController {

    @Resource
    private ExampleService exampleService;

    @Override
    public Result<Object> save(){
        exampleService.save();
        return Response.ok(ResponseCode.SUCCESS,1L);
    }
}
