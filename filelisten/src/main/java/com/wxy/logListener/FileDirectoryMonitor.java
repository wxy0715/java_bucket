package com.cjree.logListener;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 文件目录监控器
 */
public class FileDirectoryMonitor {
    private final static Logger logger = LoggerFactory.getLogger(FileDirectoryMonitor.class);
    private static volatile FileDirectoryMonitor fileDirectoryMonitor;
    final static Map<String,FileAlterationObserver> FILEDIRECTORYMAP = new HashMap<>(); //监听文件目录Map
    private FileAlterationMonitor monitor;

    private FileDirectoryMonitor(){}

    public void handleLog(String logDirPath,String suffixFile){
        try {
            if(logDirPath!=null && logDirPath.length()>0){
                File directory = new File(logDirPath);
                if(directory.exists()){
                    logger.info("监听日志文件目录--------->{},监听日志文件后缀----->{}",logDirPath,suffixFile);
                    // 轮询间隔 5 秒
                    long interval = TimeUnit.SECONDS.toMillis(1);
                    // 创建一个文件观察器用于处理文件的格式
                    FileAlterationObserver observer = new FileAlterationObserver(directory, FileFilterUtils.and(
                            FileFilterUtils.fileFileFilter(),FileFilterUtils.suffixFileFilter(suffixFile)));
                    //设置文件变化监听器
                    LogFileListener fileListener = new LogFileListener();
                    fileListener.init(directory,suffixFile);
                    observer.addListener(fileListener);
                    if(monitor==null){
                        monitor = new FileAlterationMonitor(interval);
                        monitor.start();
                    }
                    monitor.addObserver(observer);
                    FILEDIRECTORYMAP.put(logDirPath,observer);
                }else{
                    logger.info("日志文件目录{}不存在！",logDirPath);
                }
            }
        } catch (Exception e) {
            logger.error("日志目录监控异常",e);
        }
    }

    public void handleLog(String logDirPath){
        String suffixFile = ".log";
        handleLog(logDirPath, suffixFile);
    }

    public void remove(String logDirPath){
        logger.info("停止监听日志文件目录:{}！",logDirPath);
        if(monitor!=null){
            try {
                FileAlterationObserver observer = FILEDIRECTORYMAP.get(logDirPath);
                if(observer!=null){
                    monitor.removeObserver(observer);
                    observer.destroy();
                    FILEDIRECTORYMAP.remove(logDirPath);
                }

                //停止日志文件监听
                List<String> romoveLogFiles = new ArrayList<>();
                Set<String> set = LogFileListener.LOGFILELISTENERMAP.keySet();
                for (String logPath: set) {
                    String logDir = logPath.substring(0,logPath.lastIndexOf(File.separator));
                    if(logDir.equals(logDirPath)){
                        romoveLogFiles.add(logPath);
                    }
                }

                for (String logFilePath:romoveLogFiles) {
                    LogFileListener.LOGFILELISTENERMAP.get(logFilePath).stop();
                    LogFileListener.LOGFILELISTENERMAP.remove(logFilePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static FileDirectoryMonitor getInstance() {
        try {
            if (null == fileDirectoryMonitor) {
                Thread.sleep(100);
                synchronized (FileDirectoryMonitor.class) {
                    if(null == fileDirectoryMonitor) {
                        fileDirectoryMonitor = new FileDirectoryMonitor();
                    }
                }
            }
        } catch (InterruptedException e) {
            logger.error("获取FileDirectoryMonitor实例对象异常",e);
        }
        return fileDirectoryMonitor;
    }
}
