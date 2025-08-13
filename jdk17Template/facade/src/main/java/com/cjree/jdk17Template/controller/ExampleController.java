package com.cjree.jdk17Template.controller;

import com.cjree.core.common.Result;
import com.cjree.core.model.common.IdCmd;
import com.cjree.core.model.validate.QueryPage;
import com.cjree.core.model.validate.Update;
import com.cjree.jdk17Template.dto.cmd.CreateExampleCmd;
import com.cjree.jdk17Template.dto.out.ExampleOut;
import com.cjree.jdk17Template.dto.qry.ExampleQry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("jdk17template")
@Tag(name = "example", description = "示例")
@RequestMapping("example")
public interface ExampleController {

    @Operation(description = "createExample")
    @PostMapping("pc/v1/createExample")
    Result<Object> createExample(@Validated @RequestBody CreateExampleCmd cmd);

    @Operation(description = "removeExample")
    @PostMapping("pc/v1/removeExample")
    Result<Object> removeExample(@Validated(value = {IdCmd.DeleteById.class}) @RequestBody IdCmd cmd);

    @Operation(description = "modifyExample")
    @PostMapping("pc/v1/modifyExample")
    Result<Object> modifyExample(@Validated(value = {Update.class}) @RequestBody CreateExampleCmd cmd);

    @Operation(description = "getExampleById")
    @PostMapping("pc/v1/getExampleById")
    Result<ExampleOut> getExampleById(@RequestBody IdCmd cmd);

    @PostMapping(path = "/getExampleListPage")
    @Operation(description = "查询示例列表(分页)")
    Result<ExampleOut> getExampleListPage(@Validated(value = {QueryPage.class}) @RequestBody ExampleQry query);

    @PostMapping(path = "/getExampleList")
    @Operation(description = "查询示例列表")
    Result<ExampleOut> getExampleList(@RequestBody ExampleQry query);

}
