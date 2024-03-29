package com.wxy.design.adapter.extend;

public class PrintBanner extends Banner implements Printer{

    public PrintBanner(String name) {
        super(name);
    }

    @Override
    public void printWeak(){
        showWeak();
    }

    @Override
    public void printStrong(){
        showStrong();
    }
}
