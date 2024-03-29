package com.wxy;
import com.wxy.logListener.LoadLogDirectoryRunable;
import com.wxy.logListener.LogContentQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 初始化事件类
 */
@Slf4j
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        SpringUtil.setApplicationContext(contextRefreshedEvent.getApplicationContext());

        try {
            //启动日志目录监控
            new Thread(new LoadLogDirectoryRunable()).start();
            Thread.sleep(1000);
            SpringUtil.getBean(LogContentQueueService.class).start();
        } catch (InterruptedException e) {
        }
    }
}
