package com.cjree.core.facade.api;

import com.cjree.core.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Tag(name = "实例")
@RequestMapping("health")
public interface HealthController {

    /**
     * 获取健康
     */
    @Operation(summary = "获取服务健康状态")
    @RequestMapping("/pc/v1/health")
    Result<Object> getHealth(@RequestParam(name = "type", required = false) String type);
}
