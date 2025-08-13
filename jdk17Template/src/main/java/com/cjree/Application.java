package com.cjree;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableDiscoveryClient
@EnableAsync
@EnableFeignClients(basePackages = {
        "com.cjree.some-api"
        ,"com.cjree.other-api"

})
@Slf4j
public class Application {

    public static void main(String[] args) {
        StopWatch adjust = new StopWatch("core-template");
        adjust.start();
        SpringApplication.run(Application.class, args);
        adjust.stop();
        log.info("服务启动完成,耗时：{}秒", adjust.getTime(TimeUnit.SECONDS));
    }

}
