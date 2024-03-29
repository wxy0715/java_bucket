package com.wxy.design.adapter.trust;

public class Banner {
    private String name;
    public Banner(String name){
        this.name = name;
    }

    public void showWeak(){
        System.out.println(name+"weak");
    }

    public void showStrong(){
        System.out.println(name+"strong");
    }
}
