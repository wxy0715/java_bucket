package com.cjree.core.common.base;

import com.cjree.core.common.Result;
import com.cjree.core.common.exception.Error;

import java.util.Objects;

/**
 * 基础异常类，此类为抽象类，所有的系统异常类都要继承该类
 */
public abstract class BaseException extends RuntimeException {

    private Error response;

    public BaseException(Error response) {
        super(response.getMessage());
        this.response = response;
    }

    public BaseException(Throwable e, Error response) {
        super(e.getMessage() + response.getMessage(), e);
        this.response = response;
    }

    public void handler(Result<Object> result) {
        result.setCode(response.getResponseCode().value());
        result.setDescription(Objects.isNull(response.getMessage()) ? response.getResponseCode().message() : response.getMessage());
        result.setCurrentTime(System.currentTimeMillis());
    }

}
