package com.wxy.utils.result.handler;

import com.wxy.utils.result.BusinessException;
import com.wxy.utils.result.DataResult;
import com.wxy.utils.result.code.BaseResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * @ClassName: RestExceptionHandler
 * @Version: 0.0.1
 */
@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    /** wxy- 最大的异常**/
    @ExceptionHandler(Exception.class)
    public DataResult handleException(Exception e){
        log.error("handleException....{}",e);
        return DataResult.getResult(BaseResponseCode.SYSTEM_ERROR);
    }

    /** wxy- 自定义异常**/
    @ExceptionHandler(value = BusinessException.class)
    public DataResult businessException(BusinessException e){
        log.error("businessException,{},{}",e.getLocalizedMessage(),e);
        return DataResult.getResult(e.getCode(),e.getMsg());
    }

    /** wxy- 空指针异常**/
    @ExceptionHandler(NullPointerException.class)
    public DataResult nullException(NullPointerException e){
        log.error("NullPointerException,{},{}",e.getLocalizedMessage(),e);
        return DataResult.getResult(BaseResponseCode.SYSTEM_ERROR);
    }

    /** wxy- 类型转换异常**/
    @ExceptionHandler(ClassCastException.class)
    public DataResult nullException(ClassCastException e){
        log.error("ClassCastException,{},{}",e.getLocalizedMessage(),e);
        return DataResult.getResult(BaseResponseCode.SYSTEM_ERROR);
    }

    /**
     * 处理validation 框架异常
     * @throws
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    <T> DataResult<T> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.error("methodArgumentNotValidExceptionHandler bindingResult.allErrors():{},exception:{}", e.getBindingResult().getAllErrors(), e);
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        return createValidExceptionResp(errors);
    }
    private <T> DataResult<T> createValidExceptionResp(List<ObjectError> errors) {
        String[] msgs = new String[errors.size()];
        int i = 0;
        for (ObjectError error : errors) {
            msgs[i] = error.getDefaultMessage();
            log.info("msg={}",msgs[i]);
            i++;
        }
        return DataResult.getResult(BaseResponseCode.METHOD_IDENTITY_ERROR.getCode(), msgs[0]);
    }

}
