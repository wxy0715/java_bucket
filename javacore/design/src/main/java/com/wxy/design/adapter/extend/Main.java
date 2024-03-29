package com.wxy.design.adapter.extend;

/**
 * @author wxy
 * @description 适配器继承模式
 * @date 2022/3/3 13:31
 */
public class Main {
    public static void main(String[] args) {
        PrintBanner hah = new PrintBanner("hah");
        hah.printStrong();
        hah.printWeak();
    }
}
