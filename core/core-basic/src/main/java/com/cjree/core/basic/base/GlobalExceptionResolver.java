package com.cjree.core.basic.base;

import com.cjree.core.common.ResponseCode;
import com.cjree.core.common.Result;
import com.cjree.core.common.base.BaseException;
import com.cjree.core.common.log.TLogContext;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionResolver {

    @ExceptionHandler(value = Exception.class)
    public Result<Object> businessExceptionResolver(Exception e) {
        log.error(e.getMessage(), e);
        Result<Object> result = new Result<Object>();
        result.setTraceId(TLogContext.getTraceId());
        result.setSpanId(TLogContext.getSpanId());
        if (e instanceof BaseException) {
            ((BaseException) e).handler(result);
        }else if (e instanceof ValidationException) {
            result.setCode(ResponseCode.DATA_VERIFY_EXCEPTION.value());
            String[] split = e.getMessage().split(",");
            List<String> list = Arrays.asList(split);
            StringBuilder sb = new StringBuilder();
            for (String str : list) {
                sb.append(str.substring(str.lastIndexOf(":") + 1)).append(";");
            }
            String defaultMessage = sb.toString().substring(0, sb.length() - 1);
            result.setDescription(ResponseCode.DATA_VERIFY_EXCEPTION.message() + " : " + defaultMessage);
            result.setCurrentTime(System.currentTimeMillis());
        } else if (e instanceof MethodArgumentNotValidException) {
            StringBuilder defaultMessage = new StringBuilder();
            int errorCount = ((MethodArgumentNotValidException) e).getBindingResult().getErrorCount();
            for (int i = 0; i <errorCount;i++) {
                defaultMessage.append(((MethodArgumentNotValidException) e).getBindingResult().getAllErrors().get(i).getDefaultMessage()).append(";");
            }
            result.setCode(ResponseCode.DATA_VERIFY_EXCEPTION.value());
            result.setDescription(ResponseCode.DATA_VERIFY_EXCEPTION.message() + " : " + defaultMessage);
            result.setCurrentTime(System.currentTimeMillis());
        } else if (e instanceof HttpMessageNotReadableException) {
            result.setCode(ResponseCode.DATA_VERIFY_NULL_POINTER.value());
            result.setDescription(ResponseCode.DATA_VERIFY_NULL_POINTER.value());
            result.setCurrentTime(System.currentTimeMillis());
        } else if (e instanceof RuntimeException) {
            result.setCode(ResponseCode.SERVER_INTERNAL_EXCEPTION.value());
            result.setDescription(ResponseCode.SERVER_INTERNAL_EXCEPTION.message());
            result.setCurrentTime(System.currentTimeMillis());
        } else {
            result.setCode(ResponseCode.SERVER_INTERNAL_EXCEPTION.value());
            result.setDescription(ResponseCode.SERVER_INTERNAL_EXCEPTION.message());
            result.setCurrentTime(System.currentTimeMillis());
        }
        return result;
    }

}
