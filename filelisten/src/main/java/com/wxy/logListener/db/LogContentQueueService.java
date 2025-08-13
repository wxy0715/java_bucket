package com.cjree.logListener.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Description: 日志内容服务，主要将监听到的日志内容，写入数据库
 * @Auther:wxy
 * @Date:2020/9/18
 */
@Service
public class LogContentQueueService extends Thread{
    private final static Logger logger = LoggerFactory.getLogger(LogContentQueueService.class);
    public static final LinkedBlockingQueue<String> logQueue = new LinkedBlockingQueue<>(10000);
    @Override
    public void run() {
        while (true){
            try {
                String logContent = logQueue.poll();
                if(logContent!=null){
                    logContent = "ERR_"+logContent.split("ERR_")[1];
                    logger.info("监听文件增加内容{}",logContent);
                }else{
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                logger.error("插入日志内容异常",e);
            }
        }
    }
}
