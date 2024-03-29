package com.wxy.javacore.bio.bytes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ByteArrayStreamDemo {

    public static void main(String[] args) {
        String str = "HELLOWORLD"; // 定义一个字符串，全部由大写字母组成
        ByteArrayInputStream bis = new ByteArrayInputStream(str.getBytes());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 准备从内存ByteArrayInputStream中读取内容
        int temp = 0;
        while ((temp = bis.read()) != -1) {
            char c = (char) temp; // 读取的数字变为字符
            bos.write(Character.toLowerCase(c)); // 将字符变为小写
        }
        // 所有的数据就全部都在ByteArrayOutputStream中
        String newStr = bos.toString(); // 取出内容
        try {
            bis.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(newStr);
    }

}
