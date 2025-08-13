package com.cjree.logListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 释放并重新监听日志文件
 */
public class ReleaseFileRunable implements Runnable{
    private final static Logger logger = LoggerFactory.getLogger(LogContentListener.class);
    private LogContentListener logContentListener;
    private String filePath;
    @Override
    public void run() {
        try {
            if(logContentListener!=null){
                logger.info("释放{}日志监听开始.....",filePath);
                logger.info("停止{}日志监听.....",filePath);
                logContentListener.stop();
                LogFileListener.LOGFILELISTENERMAP.remove(filePath);
                logger.info("休眠{}日志30秒释放监听.....",filePath);
                Thread.sleep(1000*30);
                logger.info("重新监听{}日志3.....",filePath);
                LogContentListener logContentListener = new LogContentListener();
                logContentListener.handleLog(filePath);
                LogFileListener.LOGFILELISTENERMAP.put(filePath,logContentListener);
                LogFileListener.RELEASELOGFILEMAP.remove(filePath);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setLogContentListener(LogContentListener logContentListener) {
        this.logContentListener = logContentListener;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
