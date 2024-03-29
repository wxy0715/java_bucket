package com.wxy.design.adapter.trust;


/**
 * @author wxy
 * @description 适配器委派模式
 * @date 2022/3/3 13:31
 */
public class Main {
    public static void main(String[] args) {
        PrintBanner hah = new PrintBanner(new Banner("哈哈"));
        hah.printStrong();
        hah.printWeak();
    }
}
