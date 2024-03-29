package com.wxy.elk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@EnableScheduling
public class AppScheduledJobs {
    private static final Logger log = LoggerFactory.getLogger(Class.class);
    @Scheduled(cron = "*/5 * * * * ?")
    public void test() {
        log.debug("==================================================================================");
        log.error("<<<<<< error Start: 【{}】 >>>>>>", LocalDateTime.now());
        log.warn("<<<<<< warn Start: 【{}】 >>>>>>", LocalDateTime.now());
        log.info("<<<<<< info Start: 【{}】 >>>>>>", LocalDateTime.now());
        log.debug("<<<<<< debug Start: 【{}】 >>>>>>", LocalDateTime.now());
    }
}
