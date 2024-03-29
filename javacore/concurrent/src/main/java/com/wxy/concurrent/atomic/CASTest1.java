package com.wxy.concurrent.atomic;

import java.util.concurrent.atomic.AtomicInteger;

public class CASTest1 {
    // CAS compareAndSet : 比较并交换！
    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(2020);
        // 期望、更新
        // public final boolean compareAndSet(int expect, int update)
        // 如果我期望的值达到了，那么就更新;否则，就不更新, CAS 是CPU的并发原语！

        System.out.println(atomicInteger.compareAndSet(2020, 2021));
        System.out.println(atomicInteger.get());
        System.out.println(atomicInteger.compareAndSet(2021, 2020));
        System.out.println(atomicInteger.get());

        atomicInteger.incrementAndGet();

        System.out.println(atomicInteger.compareAndSet(2020, 6666));
        System.out.println(atomicInteger.get());
    }
}