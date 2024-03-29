package com.wxy.retry.service.impl;

import com.wxy.retry.service.IBusinessService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;

@Slf4j
@Service
public class BusinessImpl implements IBusinessService {

    /**
     * @Retryable 参数说明
     * value：抛出指定异常才会重试
     * maxAttempts：最大重试次数，默认3次
     * backoff：重试等待策略
     * recover：执行回调方法名称，必须在当前类中 -- tips:旧版本中无此参数，好像会自动对应失败回调方法
     */
    @Override
    @SneakyThrows(Exception.class)
    @Retryable(value = RetryException.class, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5), recover = "recover")
    public String retry(String msg) {
        log.info("test被调用,时间：" + new Date());
        final int a = 3;
        int num = new SecureRandom().nextInt();
        if (num % a == 0) {
            log.info("服务调用正常！");
            return "OK";
        }
        log.info("服务调用不正常。。。");
        throw new RetryException("服务调用不正常。。。");
    }

    @Recover
    public String recover(RetryException e, String msg) {
        log.info("执行回调方法: {}", msg);
        return "FAIL";
    }

}
