package com.cjree.core.basic.base;

import com.cjree.core.common.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.cjree.core.basic.common.Response.ok;

/**
 * 控制器基础类，封装通用查询方法
 */
@Slf4j
public abstract class BaseController<T extends BaseModel, S extends BaseService<T>> {

    @Autowired
    protected S service;

    @PostMapping(value = "pc/v1/page")
    @Operation(summary = "查询分页")
    public Object page(@RequestBody T t) {
        return ok(ResponseCode.SUCCESS, service.page(t, t.createPagination()));
    }

    @PostMapping(value = "pc/v1/list")
    @Operation(summary = "查询列表")
    public Object list(@RequestBody T t) {
        return ok(ResponseCode.SUCCESS, service.list(t));
    }

    @GetMapping(value = "pc/v1/unionPage")
    @Operation(summary = "联表查询分页")
    public Object unionPage(T t) {
        return ok(ResponseCode.SUCCESS, service.unionPage(t, t.createPagination()));
    }

    @PostMapping(value = "pc/v1/unionList")
    @Operation(summary = "联表查询列表")
    public Object unionList(@RequestBody T t) {
        return ok(ResponseCode.SUCCESS, service.unionList(t));
    }

    @PostMapping(value = "pc/v1/get")
    @Operation(summary = "查询单个记录")
    public Object get(@RequestBody T t) {
        return ok(ResponseCode.SUCCESS, service.get(t.getId()));
    }

    @PostMapping(value = "pc/v1/update")
    @Operation(summary = "更新单个记录")
    public Object update(@RequestBody T t) {
        return ok(ResponseCode.SUCCESS, service.update(t));
    }

    @PostMapping(value = "pc/v1/add")
    @Operation(summary = "新增单个记录")
    public Object add(@RequestBody T t) {
        return ok(ResponseCode.SUCCESS, service.insert(t));
    }

    @PostMapping(value = "pc/v1/physicalDelete")
    @Operation(summary = "物理删除单个记录")
    public Object physicalDelete(@RequestBody T t) {
        service.physicalDelete(t.getId());
        return ok(ResponseCode.SUCCESS);
    }

    @PostMapping(value = "pc/v1/logicDelete")
    @Operation(summary = "逻辑删除单个记录")
    public Object logicDelete(@RequestBody T t) {
        service.logicDelete(t.getId());
        return ok(ResponseCode.SUCCESS);
    }

}
