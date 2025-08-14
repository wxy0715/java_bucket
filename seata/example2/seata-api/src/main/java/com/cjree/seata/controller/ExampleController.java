package com.cjree.seata.controller;

import com.cjree.core.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("user2")
@Tag(name = "example", description = "示例")
@RequestMapping("example")
public interface ExampleController {

    @Operation(description = "save")
    @PostMapping("save")
    Result<Object> save();


}
