package com.wxy.jvm.classloader;

public class SubClass extends SuperClass {

    static {
        System.out.println("SubClass init!");
    }
}
