package com.wxy.concurrent.completablefuture;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
public class CompletableFutureDemo {

    public static void main(String[] args) throws Exception {
        CompletableFuture<ArrayList<Long>> cf1 = CompletableFuture.supplyAsync(() -> {
            ArrayList<Long> objects = new ArrayList<>();
            objects.add(1L);
            try {
                System.out.println(Thread.currentThread() + " cf1 do something....");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("cf1 任务完成");
            return objects;
        });

        CompletableFuture<ArrayList<Long>> cf3 = CompletableFuture.supplyAsync(() -> {
            ArrayList<Long> objects = new ArrayList<>();
            objects.add(1L);
            try {
                System.out.println(Thread.currentThread() + " cf2 do something....");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("cf3 任务完成");
            return objects;
        });
        CompletableFuture<Void> cfAll = CompletableFuture.allOf(cf1, cf3);
        System.out.println("cfAll结果->" + cfAll.get());
        CompletableFuture<List<Long>> listCompletableFuture = cfAll.thenApply(v -> Stream.of(cf1, cf3).map(CompletableFuture::join).flatMap(List::stream).collect(Collectors.toList()));
        List<Long> join = listCompletableFuture.join();
        System.out.println("cfAll结果->" + join);
        // 两个CompletableFuture执行异步查询:
        CompletableFuture<String> cfQueryFromSina = CompletableFuture.supplyAsync(() -> {
            return queryCode("中国石油", "https://finance.sina.com.cn/code/");
        });
        CompletableFuture<String> cfQueryFrom163 = CompletableFuture.supplyAsync(() -> {
            return queryCode("中国石油", "https://money.163.com/code/");
        });

        // 用anyOf合并为一个新的CompletableFuture:
        CompletableFuture<Object> cfQuery = CompletableFuture.anyOf(cfQueryFromSina, cfQueryFrom163);

        // 两个CompletableFuture执行异步查询:
        CompletableFuture<Double> cfFetchFromSina = cfQuery.thenApplyAsync((code) -> {
            return fetchPrice((String) code, "https://finance.sina.com.cn/price/");
        });
        CompletableFuture<Double> cfFetchFrom163 = cfQuery.thenApplyAsync((code) -> {
            return fetchPrice((String) code, "https://money.163.com/price/");
        });

        // 用anyOf合并为一个新的CompletableFuture:
        CompletableFuture<Object> cfFetch = CompletableFuture.anyOf(cfFetchFromSina, cfFetchFrom163);

        // 最终结果:
        cfFetch.thenAccept((result) -> {
            System.out.println("price: " + result);
        });
        // 主线程不要立刻结束，否则CompletableFuture默认使用的线程池会立刻关闭:
        Thread.sleep(200);

        CompletableFuture<String> future = CompletableFuture.supplyAsync(()->{
            try {
                TimeUnit.SECONDS.sleep(1);
            }catch (InterruptedException e){
                System.out.println("异常："+e);
            }
            //模拟异常
            int i = 12 / 0;
            System.out.println("执行结束");
            return "异步方法：future";
        });
        future.thenApply(s -> s).exceptionally(throwable -> {
            System.out.println("执行失败"+throwable);
            return "异步异常";
        }).join();
        CompletableFuture<Void> future1 = CompletableFuture
                .supplyAsync(() -> {
                    int number = 10;
                    System.out.println("任务1：" + number);
                    return number;
                }).thenAccept(number ->
                        System.out.println("任务2：" + number * 5)
                );
        System.out.println("最终结果：" + future1.get());
    }

    static String queryCode(String name, String url) {
        System.out.println("query code from " + url + "...");
        try {
            Thread.sleep((long) (Math.random() * 100));
        } catch (InterruptedException e) {
        }
        return "601857";
    }

    static Double fetchPrice(String code, String url) {
        System.out.println("query price from " + url + "...");
        try {
            Thread.sleep((long) (Math.random() * 100));
        } catch (InterruptedException e) {
        }
        return 5 + Math.random() * 20;
    }

}
