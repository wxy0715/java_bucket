package com.wxy.concurrent.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ConcurrentHashMap 示例
 *
 * @author <a href="mailto:forbreak@163.com">Zhang Peng</a>
 * @since 2018/5/16
 */
public class ConcurrentHashMapDemo {

    public static void main(String[] args) throws InterruptedException {

        // HashMap 在并发迭代访问时会抛出 ConcurrentModificationException 异常
        // Map<Integer, Character> map = new HashMap<>();
        final Map<Integer, Character> map = new ConcurrentHashMap<>();

        Thread wthread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("写操作线程开始执行");
                for (int i = 0; i < 26; i++) {
                    map.put(i, (char) ('a' + i));
                }
            }
        });
        Thread rthread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("读操作线程开始执行");
                for (Integer key : map.keySet()) {
                    System.out.println(key + " - " + map.get(key));
                }
            }
        });
        wthread.start();
        rthread.start();
        Thread.sleep(1000);
    }

}
