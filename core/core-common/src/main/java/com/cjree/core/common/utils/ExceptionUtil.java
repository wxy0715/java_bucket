package com.cjree.core.common.utils;

import com.cjree.core.common.ResponseCode;
import com.cjree.core.common.exception.DataVerifyException;
import com.cjree.core.common.exception.Error;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.ObjectUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 关于异常的工具类
 */
public class ExceptionUtil {

    /**
     * 将CheckedException转换为UncheckedException.
     */
    public static RuntimeException unchecked(Exception e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else {
            return new RuntimeException(e);
        }
    }

    /**
     * 将ErrorStack转化为String.
     */
    public static String getStackTraceAsString(Throwable e) {
        if (e == null) {
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    /**
     * 判断异常是否由某些底层的异常引起.
     */
    @SafeVarargs
    public static boolean isCausedBy(Exception ex, Class<? extends Exception>... causeExceptionClasses) {
        Throwable cause = ex.getCause();
        while (cause != null) {
            for (Class<? extends Exception> causeClass : causeExceptionClasses) {
                if (causeClass.isInstance(cause)) {
                    return true;
                }
            }
            cause = cause.getCause();
        }
        return false;
    }

    /**
     * 在request中获取异常类
     *
     * @param request
     * @return
     */
    public static Throwable getThrowable(HttpServletRequest request) {
        Throwable ex = null;
        if (request.getAttribute("exception") != null) {
            ex = (Throwable) request.getAttribute("exception");
        } else if (request.getAttribute("javax.servlet.error.exception") != null) {
            ex = (Throwable) request.getAttribute("javax.servlet.error.exception");
        }
        return ex;
    }


    /**
     * 功能描述: <br>
     * 〈〉 快速获取异常信息
     * @Param: [responseCode, message]
     * @Return: com.cjree.core.exception.DataVerifyException
     * @Author: king
     * @Date: 2020/8/21 15:45
     */
    public static DataVerifyException getException(ResponseCode responseCode, String message){
        throw new DataVerifyException(
                Error.builder()
                        .responseCode(null == responseCode?ResponseCode.FAILURE:responseCode)
                        .message(message)
                        .build()
        );
    }

    /**
     * 满足条件的报错
     * 比如flag = ObjectUtils.isEmpty(obj) 提示消息message
     * @param flag    条件
     * @param message 报错信息
     */
    public static void isTrue(boolean flag, String message) {
        if (flag) {
            getException(null,message);
        }
    }

    /**
     * 满足条件的报错
     * 比如flag = ObjectUtils.isEmpty(obj) 提示消息message
     * @param flag    条件
     * responseCode   系统应用响应状态码
     * @param message 报错信息
     */
    public static void isTrue(boolean flag, String message, ResponseCode responseCode) {
        if (flag) {
            getException(responseCode,message);
        }
    }

    /**
     * 满足条件的报错
     * 比如flag = ObjectUtils.isEmpty(obj) 提示消息message
     * @param o    对象
     * @param message 报错信息
     */
    public static void isNull(Object o, String message) {
        if (ObjectUtils.isEmpty(o)) {
            getException(null,message);
        }
    }

    /**
     * 直接报错
     * @param message 报错信息
     */
    public static void error(String message) {
        getException(null,message);
    }
}
