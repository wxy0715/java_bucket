package com.cjree.core.basic.common;

import com.cjree.core.common.ResponseCode;
import com.cjree.core.common.Result;
import com.cjree.core.common.log.TLogContext;
import com.cjree.core.common.utils.ExceptionUtil;
import com.cjree.core.model.common.Pagination;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Response {

    public static Result<Object> ok(ResponseCode responseCode) {
        return ok(responseCode.value(), responseCode.message(), new ArrayList<Object>());
    }

    public static <T> Result<T> ok(ResponseCode responseCode, Pagination<T> data) {
        return ok(responseCode.value(), responseCode.message(), data);
    }

    public static <T> Result<T> ok(ResponseCode responseCode, List<T> data) {
        return ok(responseCode.value(), responseCode.message(), data);
    }

    public static <T> Result<T> ok(ResponseCode responseCode, T data) {
        List<T> param = Arrays.asList(data);
        return ok(responseCode.value(), responseCode.message(), param);
    }

    public static <T> Result<T> ok(String code, String description, Pagination<T> data) {
        if (StringUtils.isEmpty(code)) {
            code = ResponseCode.SUCCESS.value();
        }
        if (StringUtils.isEmpty(description)) {
            description = ResponseCode.SUCCESS.message();
        }
        Result<T> result = new Result<T>();
        if (data != null) {
            result.setData(data.getRecords());
            result.setPageIndex(data.getCurrent());
            result.setPageSize(data.getSize());
            result.setPageCount(data.getPages());
            result.setTotal(data.getTotal());
        }
        result.setCode(code);
        result.setDescription(description);
        result.setCurrentTime(System.currentTimeMillis());
        result.setTraceId(TLogContext.getTraceId());
        return result;
    }

    public static <T> Result<T> ok(String code, String description, List<T> data) {
        if (StringUtils.isEmpty(code)) {
            code = ResponseCode.SUCCESS.value();
        }
        if (StringUtils.isEmpty(description)) {
            description = ResponseCode.SUCCESS.message();
        }
        Result<T> result = new Result<T>();
        if (data != null) {
            data = data.stream().filter(Objects::nonNull).collect(Collectors.toList());
            result.setData(data);
            result.setTotal(data.size());
        }
        result.setCode(code);
        result.setDescription(description);
        result.setCurrentTime(System.currentTimeMillis());
        result.setTraceId(TLogContext.getTraceId());
        return result;
    }

    public static Result<Object> success() {
        return ok(ResponseCode.SUCCESS);
    }

    public static Result<Object> fail() {
        return ok(ResponseCode.FAILURE);
    }

    /**
     * 验证远程调用是否错误,并且返回错误---集合
     * @param result 远程调用结果
     * @return
     */
    public static <T> List<T> postList(Result<T> result){
        if (!Objects.equals(result.getCode(), ResponseCode.SUCCESS.value())) {
            throw ExceptionUtil.getException(null, result.getDescription());
        }
        return result.getData();
    }

    /**
     * 验证远程调用是否错误,并且返回错误---单个
     * @param result 远程调用结果
     * @return
     */
    public static <T> T post(Result<T> result){
        if (!Objects.equals(result.getCode(), ResponseCode.SUCCESS.value())) {
            throw ExceptionUtil.getException(null, result.getDescription());
        }
        List<T> data = result.getData();
        if (ObjectUtils.isEmpty(data)) {
            return null;
        }
        return data.get(0);
    }

    /**
     * 验证远程调用是否错误,并且返回错误---分页
     * @param result 远程调用结果
     * @return
     */
    public static <T> Pagination<T> postPagination(Result<T> result){
        if (!Objects.equals(result.getCode(), ResponseCode.SUCCESS.value())) {
            throw ExceptionUtil.getException(null, result.getDescription());
        }
        Pagination<T> pagination = new Pagination<T>();
        pagination.setTotal(result.getTotal());
        pagination.setRecords(result.getData());
        return pagination;
    }

}
