package com.cjree.seata.controller;

import com.cjree.core.common.ResponseCode;
import com.cjree.core.common.Result;
import com.cjree.core.model.common.IdCmd;
import com.cjree.jdk17Template.controller.ExampleController;
import com.cjree.jdk17Template.dto.cmd.CreateExampleCmd;
import com.cjree.jdk17Template.dto.out.ExampleOut;
import com.cjree.jdk17Template.dto.qry.ExampleQry;
import com.cjree.jdk17Template.service.api.ExampleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cjree.core.basic.common.Response.ok;


@RestController
@Slf4j
public class ExampleControllerImpl implements ExampleController {

    @Autowired
    private ExampleService service;

    @Override
    public Result<Object> createExample(CreateExampleCmd cmd) {
        return ok(ResponseCode.SUCCESS, service.addExample(cmd));
    }

    @Override
    public Result<Object> removeExample(IdCmd cmd) {
        service.removeExample(cmd);
        return ok(ResponseCode.SUCCESS);
    }

    @Override
    public Result<Object> modifyExample(CreateExampleCmd cmd) {
        return ok(ResponseCode.SUCCESS, service.modifyExample(cmd));
    }

    @Override
    public Result<ExampleOut> getExampleById(IdCmd cmd) {
        return ok(ResponseCode.SUCCESS, service.getExampleById(cmd));
    }

    @Override
    public Result<ExampleOut> getExampleListPage(ExampleQry query) {
        return ok(ResponseCode.SUCCESS, service.getExampleListPage(query));
    }

    @Override
    public Result<ExampleOut> getExampleList(ExampleQry query) {
        return ok(ResponseCode.SUCCESS, service.getExampleList(query));
    }

    @GetMapping("/test1")
    @Operation(description = "测试异步线程")
    public Result<Long> test1() {
        log.info("开始调用异步方法");
        service.asyncMethod();
        return ok(ResponseCode.SUCCESS, 1L);
    }
}
