package com.wxy.algorithm.search;

import java.lang.reflect.Array;
import java.util.Random;

/**
 * 二分查找法:
 * */
public class TwoSearch {
    public static int size = 1000000;
    public static long[] arr = seq(long[].class, size);
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        System.out.println(searchF(arr));;
        long end = System.currentTimeMillis();
        System.out.println(size+"二分查找法花费时间为:"+(end-start)+"毫秒");
    }

    /** 递归*/
    private static int searchR(long[] arr,int left, int right, int target) {
        if (left > right) {
            return -1;
        }
        int mid = left + (right-left) / 2;
        if (arr[mid] > target) {
            return searchR(arr,left,mid-1, target);
        } else if(arr[mid] < target){
            return searchR(arr,mid+1,right, target);
        } else {
            return mid;
        }
    }
    /** 循环*/
    private static int searchF(long[] arr) {
        int left = 0;
        int right = arr.length-1;
        while (left <= right) {
            int mid = left + (right-left) / 2;
            if(arr[mid] > 34900) {
                right = mid-1;
            }else if (arr[mid] < 34900) {
                left = mid + 1;
            } else {
                return mid;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    public static <T> T seq(Class<T> arrayClass, int size) {
        Class<?> componentType = arrayClass.getComponentType();
        String componentName = componentType.getName();
        T array = (T) Array.newInstance(componentType, size);
        Random random = new Random();
        if("char".equals(componentName) || Character.class.getName().equals(componentName)) {
            for (int i = 0; i < size; i++) {
                Array.set(array, i, (char)(random.nextInt(1000000000)));
            }
        }else {
            for (int i = 0; i < size; i++) {
                Array.set(array, i, i);
            }
        }
        return array;
    }

    public static void swap(long[] arr,int a,int b){
        if (a != b && arr[a] != arr[b]) {
            arr[a] = arr[a]+arr[b];
            arr[b] = arr[a]-arr[b];
            arr[a] = arr[a]-arr[b];
        }
    }
}
