package com.wxy.javacore.io;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class RandomAccessFile {
    @Test
    public void write() throws IOException {

        // 指定要操作的文件
        File f = new File("temp.log");

        // 声明RandomAccessFile类的对象，读写模式，如果文件不存在，会自动创建
        java.io.RandomAccessFile rdf = new java.io.RandomAccessFile(f, "rw");

        // 写入一组记录
        String name = "zhangsan";
        int age = 30;
        rdf.writeBytes(name);
        rdf.writeInt(age);

        // 写入一组记录
        name = "lisi    ";
        age = 31;
        rdf.writeBytes(name);
        rdf.writeInt(age);

        // 写入一组记录
        name = "wangwu  ";
        age = 32;
        rdf.writeBytes(name);
        rdf.writeInt(age);

        // 关闭
        rdf.close();
    }

    @Test
    public void read() throws IOException {

        // 指定要操作的文件
        File f = new File("temp.log");

        // 声明RandomAccessFile类的对象，以只读的方式打开文件
        java.io.RandomAccessFile rdf = null;
        rdf = new java.io.RandomAccessFile(f, "r");

        byte[] b = new byte[8];

        // 读取第二个人的信息，意味着要空出第一个人的信息
        rdf.skipBytes(12); // 跳过第一个人的信息
        for (int i = 0; i < b.length; i++) {
            b[i] = rdf.readByte(); // 读取一个字节
        }
        String name = new String(b); // 将读取出来的byte数组变为字符串

        int age = rdf.readInt();  // 读取数字
        System.out.println("第二个人的信息 --> 姓名：" + name + "；年龄：" + age);

        // 读取第一个人的信息
        rdf.seek(0); // 指针回到文件的开头
        for (int i = 0; i < b.length; i++) {
            b[i] = rdf.readByte(); // 读取一个字节
        }
        name = new String(b); // 将读取出来的byte数组变为字符串
        age = rdf.readInt(); // 读取数字
        System.out.println("第一个人的信息 --> 姓名：" + name + "；年龄：" + age);

        // 读取第三个人的信息
        rdf.skipBytes(12); // 空出第二个人的信息
        for (int i = 0; i < b.length; i++) {
            b[i] = rdf.readByte(); // 读取一个字节
        }
        name = new String(b); // 将读取出来的byte数组变为字符串
        age = rdf.readInt(); // 读取数字
        System.out.println("第三个人的信息 --> 姓名：" + name + "；年龄：" + age);

        // 关闭
        rdf.close();
    }
}
