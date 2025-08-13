package com.cjree.logListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 监听入口
 */
public class LoadLogDirectoryRunable implements Runnable{
    private final static Logger logger = LoggerFactory.getLogger(LoadLogDirectoryRunable.class);
    @Override
    public void run() {
        while (true){
            String path = "D:\\test\\config";
            List<String> logDirs = new ArrayList<>();
            InputStream is = null;
            InputStreamReader inputReader = null;
            BufferedReader bf = null;
            try {
                logger.info("==========日志监控加载{}文件，读取监控日志目录开始........",path);
                is = new FileInputStream(path);
                inputReader = new InputStreamReader(is);
                bf = new BufferedReader(inputReader);
                String str;
                // 按行读取字符串
                while ((str = bf.readLine()) != null) {
                    File logDirFile = new File(str);
                    logDirs.add(logDirFile.getPath());
                }
                bf.close();
                inputReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(bf!=null){
                    try {
                        bf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(inputReader!=null){
                    try {
                        inputReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(is!=null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            //递归获取所有子目录
            Map<String,String> logs = new HashMap<>();
            for (String dirPath:logDirs) {
                getAllDirPaths(dirPath,logs);
            }

            //获取移除的目录
            List<String> removeLogPaths = new ArrayList<>();
            if(!FileDirectoryMonitor.FILEDIRECTORYMAP.keySet().isEmpty()){
                for (String logPath:FileDirectoryMonitor.FILEDIRECTORYMAP.keySet()) {
                    if(logs.get(logPath)==null){
                        removeLogPaths.add(logPath);
                    }
                }

                //递归获取所有要移除的子目录
                Map<String,String> removelogDirs = new HashMap<>();
                for (String dirPath:removeLogPaths) {
                    getAllDirPaths(dirPath,removelogDirs);
                }


                //停止已移除目录的监听
                for (String logPath :removelogDirs.keySet()) {
                    if(FileDirectoryMonitor.FILEDIRECTORYMAP.get(logPath)!=null){
                        logger.info("【{}】日志目录已移除，停止目录监听.........",logPath);
                        FileDirectoryMonitor.getInstance().remove(logPath);
                    }
                }
            }

            //加载新目录
            for (String logPath:logs.values()) {
                File file = new File(logPath);
                if(file.exists() && FileDirectoryMonitor.FILEDIRECTORYMAP.get(file.getPath())==null){
                    logger.info("添加【{}】日志目录目录监听.........",file.getPath());
                    FileDirectoryMonitor.getInstance().handleLog(file.getPath());
                }
            }
            try {
                //每5分钟扫描一次
                Thread.sleep(1000*5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void getAllDirPaths(String rootPath,Map<String,String> dirsMap){
        dirsMap.put(rootPath,rootPath);
        File[] files = new File(rootPath).listFiles();
        if (files != null && files.length>0) {
            for(File file : files){
                if(file.isDirectory()){
                    dirsMap.put(file.getPath(),file.getPath());
                    getAllDirPaths(file.getPath(), dirsMap);
                }
            }
        }
    }
}
