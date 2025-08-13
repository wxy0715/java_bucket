package com.cjree.core.basic.aop;

import com.cjree.core.basic.util.TUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * 切面把用户请求的数据字符去除前后空格
 */
@Slf4j
@Aspect
@Component
public class ArgsAspect implements Ordered {

    @Pointcut("execution(* com.cjree..*.controller.*.*(..))")
    public void point() {
    }

    @Pointcut("execution(* com.cjree..*.interfaces.api.*.*(..))")
    public void point0() {
    }

    @Before("point()||point0()")
    public void before(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            try {
                TUtils.trimBeanString(arg);
            } catch (Exception e) {
                log.info("参数处理错误:{}",e.getMessage());
            }
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }

}
