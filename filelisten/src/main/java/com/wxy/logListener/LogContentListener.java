package com.cjree.logListener;

import com.cjree.logListener.db.LogContentQueueService;
import com.cjree.logListener.tailer.TailerCustom;
import com.cjree.logListener.tailer.TailerListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 日志内容监听器，用于动态监听日志写入的内容
 */
public class LogContentListener {
    private final static Logger logger = LoggerFactory.getLogger(LogContentListener.class);
    private TailerCustom tailer;
    private String logFilePath;
    public void handleLog(final String logFilePath){
        File file = new File(logFilePath);
        try {
            this.logFilePath = logFilePath;
            tailer = new TailerCustom(file,new TailerListenerAdapter(){
                @Override
                public void fileNotFound() {  //文件没有找到
                    logger.error("日志文件{}不存在,退出日志监听............",logFilePath);
                    //没有找到文件退出监听
                    tailer.stop();
                }

                @Override
                public void fileRotated() {  //文件被外部的输入流改变
                    super.fileRotated();
                }

                @Override
                public void handle(String line) { //增加的文件的内容
                    if(line.contains("ERR_")){
//                        logger.error("读取日志文件{}内容:{}",logFilePath,line);
                        LogContentQueueService.logQueue.add(line);
                    }
                }

                @Override
                public void handle(Exception ex) {
                    super.handle(ex);
                }

            },1000,true);
            new Thread(tailer).start();
        } catch (Exception e) {
            logger.error("监听日志文件{}异常",logFilePath,e);
        }
    }

    public void stop(){
        if(tailer!=null){
            tailer.stop();
            logger.error("停止{}日志文件内容监听！",logFilePath);
        }
    }
}
