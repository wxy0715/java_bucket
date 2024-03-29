package com.wxy.java8;

import java.util.ArrayList;
import java.util.List;

public class SubList {
    public static void main(String[] args) {
        // 创建一个ArrayList
        ArrayList<String> list = new ArrayList<>();
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        // 获取子列表，范围是索引1（包含）到索引3（不包含）
        List<String> subList = list.subList(0, 2);
        System.out.println("原列表为："+list); // [A, B, C, D, E]，因为子列表视图反映了原列表的变化
        System.out.println("子列表为："+subList); // [A, B]，因为子列表视图反映了原列表的变化

        // 结构性修改,子列表是原列表的一个视图，修改原列表会影响子列表
        list.set(0, "X");
        System.out.println("改变原列表的第一个元素为X");
        System.out.println("原列表为："+list); // [X, B, C, D, E]，因为子列表视图反映了原列表的变化
        System.out.println("子列表为："+subList); // [X, B]，因为子列表视图反映了原列表的变化

        // 非结构性修改（不改变大小）通常可以同步到原列表
        subList.set(1, "Y");
        System.out.println("改变子列表的第一个元素为Y");
        System.out.println("原列表为："+list); // [X, Y, C, D, E]，因为子列表视图反映了原列表的变化
        System.out.println("子列表为："+subList); // [X, Y]，因为子列表视图反映了原列表的变化

        // 子列表上直接进行结构性修改（如 add/remove)
        subList.add("Z");
        System.out.println("子列表添加元素Z");
        System.out.println("原列表为："+list); // [X, Y, Z, C, D, E]，因为子列表视图反映了原列表的变化
        System.out.println("子列表为："+subList); // [X, Y, Z]，因为子列表视图反映了原列表的变化

        // 但是，如果在原列表上直接进行结构性修改（如 add/remove），会抛出 ConcurrentModificationException
        list.add("Z");
        System.out.println("原列表添加元素Z");
        System.out.println("原列表为："+list); // [X, Y, Z, C, D, E, Z]，因为子列表视图反映了原列表的变化
        System.out.println("子列表为："+subList); // Exception in thread "main" java.util.ConcurrentModificationException
    }
}
