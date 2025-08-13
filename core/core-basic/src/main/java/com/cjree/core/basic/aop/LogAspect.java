package com.cjree.core.basic.aop;

import com.alibaba.fastjson.JSON;
import com.cjree.core.common.constants.Constants;
import com.google.common.base.Strings;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalTime;

/**
 * 打印用户请求及响应数据日志 todo 用户
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    String A = Strings.repeat("┬", 150);
    String B = Strings.repeat("┴", 150);

    @Pointcut("execution(* com.cjree..*.controller.*.*(..))")
    public void point() {
    }

    @Pointcut("execution(* com.cjree.core.basic.base.BaseController.*(..))")
    public void point0() {
    }

    @Before("point()||point0()")
    public void before(JoinPoint joinPoint) {
        String userId = "";
        MDC.put("UserId", userId);
        MDC.put("CallTime", LocalTime.now().toString());
        Object[] args = joinPoint.getArgs();
        String info = "\n======= 访问接口:".concat(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRequestURI());
        String  realIp = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getHeader("X-Real-IP");
        MDC.put(Constants.REAL_IP, realIp);
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
            String jsonString = JSON.toJSONString(args);
            info = info.concat("\n======= 请求参数:".concat(jsonString));
        }
        log.info(info);
    }

    @AfterReturning(value = "point()||point0()", returning = "result")
    public void afterReturn(JoinPoint joinPoint, Object result) {
        LocalTime time = LocalTime.parse(MDC.get("CallTime"));
        Object[] args = joinPoint.getArgs();
        String argString = "";
        if (!ObjectUtils.isEmpty(args)) {
            boolean flag = !ObjectUtils.isEmpty(args)
                    && !(args[0] instanceof ServletResponse)
                    && !(args[0] instanceof ServletRequest)
                    && !(args[0] instanceof MultipartFile[])
                    && !(args[0] instanceof MultipartFile)
                    && !hasByteArray(args[0]);
            if (flag) {
                argString = JSON.toJSONString(args[0]);
            }
        }
        String resultString = JSON.toJSONString(result);
        log.info("\n{}", String.join(
                "\n",
                A,
                String.format("> Time:%dms", Duration.between(time, LocalTime.now()).toMillis()),
                "> 访问接口:".concat(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRequestURI()),
                "> Args:".concat(argString),
                "> Result:".concat(resultString),
                B
        ));
        MDC.remove("UserId");
        MDC.remove("CallTime");
        MDC.remove(Constants.REAL_IP);
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
