package com.wxy.design.adapter.trust;

public class PrintBanner extends Printer {
    private Banner banner;

    public PrintBanner(Banner banner) {
        this.banner = banner;
    }

    @Override
    public void printWeak(){
        banner.showWeak();
    }

    @Override
    public void printStrong(){
        banner.showStrong();
    }
}
