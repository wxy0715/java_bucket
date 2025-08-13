package com.cjree.core.facade.config;

import com.alibaba.fastjson.JSON;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;

/**
 * 外部调用日志
 * @author wxy
 * @since 2023/11/02
 */
@Slf4j
@Aspect
@Component
public class FacadeLogAspect implements Ordered {
    @Pointcut("execution(* com.cjree..*.interfaces.api.*.*(..))")
    public void point0() {
    }

    @Before("point0()")
    public void before(JoinPoint joinPoint) {
        // 根据实例id获取数据
        String url = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRequestURI();
        Object[] args = joinPoint.getArgs();
        String info = "";
        if (!ObjectUtils.isEmpty(args)) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if ((arg instanceof ServletResponse)
                        || (arg instanceof ServletRequest)
                        || (arg instanceof MultipartFile[])
                        || (arg instanceof MultipartFile)
                        || hasByteArray(args)) {
                    args[i] = null;
                }
            }
            info = info.concat("[FEIGN]请求接口:"+url+" [FEIGN]请求参数:".concat(JSON.toJSONString(args)));
        }
        log.info(info);
    }


    @AfterReturning(value = "point0()", returning = "result")
    public void afterReturn(JoinPoint joinPoint, Object result) {
        String resultString = JSON.toJSONString(result);
        if (resultString.length() > 5000) {
            resultString = "返回值过长请在debug日志中查看完整返回值，" + resultString.substring(0, 1000);
        }
        log.info("[FEIGN] 返回结果:".concat(JSON.toJSONString(resultString)));
    }

    @Override
    public int getOrder() {
        return 2;
    }


    private boolean hasByteArray(Object args) {
        try {
            boolean flag = false;
            for (Field field : args.getClass().getDeclaredFields()) {
                if (field.getType() == byte[].class) {
                    flag = true;
                    break;
                }
            }
            return flag;
        } catch (Exception e) {
            return false;
        }
    }


}
