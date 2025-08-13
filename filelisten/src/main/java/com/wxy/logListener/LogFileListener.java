package com.cjree.logListener;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日志文件监听
 */
public class LogFileListener implements FileAlterationListener {
    private final static Logger logger = LoggerFactory.getLogger(LogFileListener.class);
    private long fileSize = 1024*1024*100; //100MB
    final static Map<String,Boolean> RELEASELOGFILEMAP = new HashMap<>(); //释放文件监听Map
    final static Map<String,LogContentListener> LOGFILELISTENERMAP = new HashMap<>(); //监听文件Map

    public void init(File file,String suffixFile){
        List<File> fileList = (List<File>)FileUtils.listFiles(file,null,false);
        for (File file1:fileList) {
            if (!file1.getName().contains(".")) {
                continue;
            }
            String fileName = file1.getName();
            String suffix = file1.getName().substring(fileName.lastIndexOf("."),fileName.length());
            if(suffix.equalsIgnoreCase(suffixFile)){
                logger.info("------------初始化监听日志文件{}------------",file1.getPath());
                LogContentListener logContentListener = new LogContentListener();
                logContentListener.handleLog(file1.getPath());
                LOGFILELISTENERMAP.put(file1.getPath(),logContentListener);
            }
        }
    }
    @Override
    public void onStart(FileAlterationObserver fileAlterationObserver) {

    }

    @Override
    public void onDirectoryCreate(File file) {

    }

    @Override
    public void onDirectoryChange(File file) {

    }

    @Override
    public void onDirectoryDelete(File file) {

    }

    @Override
    public void onFileCreate(File file) {
        if(LOGFILELISTENERMAP.get(file.getPath())==null){
            logger.info("----------监听新创建日志文件{}-------------",file.getPath());
            LogContentListener logContentListener = new LogContentListener();
            logContentListener.handleLog(file.getPath());
            LOGFILELISTENERMAP.put(file.getPath(),logContentListener);
        }
    }

    @Override
    public void onFileChange(File file) {
        if(file.length()>=fileSize && (RELEASELOGFILEMAP.get(file.getPath())==null || !RELEASELOGFILEMAP.get(file.getPath()))){
            logger.info("----------{}日志文件大于{},释放日志监听----------",file.getPath(),fileSize);
            RELEASELOGFILEMAP.put(file.getPath(),true);
            ReleaseFileRunable releaseFileRunable = new ReleaseFileRunable();
            releaseFileRunable.setFilePath(file.getPath());
            releaseFileRunable.setLogContentListener(LOGFILELISTENERMAP.get(file.getPath()));
            new Thread(releaseFileRunable).start();
        }

    }

    @Override
    public void onFileDelete(File file) {
        String filePath = file.getPath();
        if(LOGFILELISTENERMAP.get(filePath)!=null){
            logger.info("-------------{}日志文件被删除，停止日志监听---------",file.getPath());
            LOGFILELISTENERMAP.get(filePath).stop();
            LOGFILELISTENERMAP.remove(filePath);
        }
    }

    @Override
    public void onStop(FileAlterationObserver fileAlterationObserver) {

    }
}
