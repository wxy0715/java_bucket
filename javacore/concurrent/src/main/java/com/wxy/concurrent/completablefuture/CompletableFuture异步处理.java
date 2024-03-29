package com.wxy.concurrent.completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CompletableFuture异步处理 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        supplyAsyncDemo();
        runAsyncDemo();
    }

    // 无返回值
    public static void runAsyncDemo() throws ExecutionException, InterruptedException {
        System.out.println("runAsyncDemo 示例");
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("结束");
        });
        future.get();
    }

    // 有返回值
    public static void supplyAsyncDemo() throws ExecutionException, InterruptedException {
        System.out.println("supplyAsyncDemo 示例");
        CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("结束");
            return System.currentTimeMillis();
        });
        long time = future.get();
        System.out.println("time = " + time);
    }

}
