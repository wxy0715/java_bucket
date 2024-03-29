package com.wxy.javacore.io;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public class PropertiesDemo {
    @Test
    public void store(){
        Properties properties = new Properties();
        properties.setProperty("wxy","王星宇");
        File file = new File("/Users/wangxingyu/area.properteis");
        try {
            properties.store(Files.newOutputStream(file.toPath()),"测试");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void load() throws IOException {
        Properties properties = new Properties();
        File file = new File("/Users/wangxingyu/area.properteis");
        properties.load(Files.newBufferedReader(file.toPath()));
        System.out.println(properties.getProperty("wxy"));
    }
}
