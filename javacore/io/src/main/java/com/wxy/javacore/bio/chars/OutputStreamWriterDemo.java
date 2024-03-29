package com.wxy.javacore.bio.chars;

import java.io.*;

/**
 * 将 OutputStream 转为 Writer
 */
public class OutputStreamWriterDemo {

    public static void main(String[] args) throws IOException {
        File f = new File("temp.log");
        Writer out = new OutputStreamWriter(new FileOutputStream(f));
        out.write("hello world!!");
        out.close();
    }

}
