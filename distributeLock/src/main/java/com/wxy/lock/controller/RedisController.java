package com.wxy.lock.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/redis")
public class RedisController {
    public ArrayList<String> dataList = new ArrayList<>(10000);

    public String sourceFile = "/Users/wangxingyu/wxy/fund/fund.txt";

    public List<String> targetFileName = new ArrayList<>();

    // todo 记录失败数据的索引,支持重试
    // 内存有限制在10m内
    @PostMapping("/set")
    public String set() throws Exception {
        // 先把大文件拆分为小文件
        split();
        // 读取拆分后文件 遍历入库

        return "ok";
    }

    public void split() throws Exception {
        long startTime = System.currentTimeMillis();
        // 先把文件拆分为n个小文件
        BufferedReader bufferedReader = new BufferedReader(new FileReader(sourceFile));
        // 总量
        int total = 0;
        // 文件游标
        int fileNumber = 1;
        String line = null;
        while((line=bufferedReader.readLine()) != null){
            total++;
            dataList.add(line);
            // 10000行时 数据写入新文件 大约2m
            if (dataList.size() == 1000) {
                // 创建文件
                importFile(fileNumber);
                fileNumber++;
            }
        }
        // 不足10000的部分
        if (dataList.size() != 0) {
            importFile(fileNumber);
        }
        bufferedReader.close();
        System.out.println("total:"+total+",文件拆分耗时"+(System.currentTimeMillis()-startTime)/1000+"秒");
    }


    public void importFile(int fileNumber) throws Exception{
        // 创建文件
        String fileName = "/Users/wangxingyu/wxy/fund/fundList/"+fileNumber+".txt";
        targetFileName.add(fileName);
        FileIoUtils.getFileByNameCreate("/Users/wangxingyu/wxy/fund/"+fileNumber+".txt",0);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(fileName)));
        for (String data : dataList) {
            bufferedWriter.write(data, 0,data.length() );
        }
        dataList.clear();
        bufferedWriter.flush();
        bufferedWriter.close();
    }

}
