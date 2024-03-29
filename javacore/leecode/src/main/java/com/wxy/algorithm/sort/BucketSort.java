package com.wxy.algorithm.sort;

import java.lang.reflect.Array;
import java.util.Random;

/**
 * 桶排序:最好O(n+k) 平均O(n2) 最坏O(n2)
 *https://www.runoob.com/w3cnote/bucket-sort.html
 * */
public class BucketSort {
    public static int size = 10000000;
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        int[] arr = seq(int[].class, size);
        buckert(arr);
        long end = System.currentTimeMillis();
        //System.out.println(Arrays.toString(arr));
        System.out.println(size+"大小的桶排序+希尔排序花费时间为:"+(end-start)+"毫秒");

        long start1 = System.currentTimeMillis();
        int[] arr1 = seq(int[].class, size);
        buckert1(arr1);
        long end1 = System.currentTimeMillis();
        //System.out.println(Arrays.toString(arr1));
        System.out.println(size+"大小的桶排序+归并排序花费时间为:"+(end1-start1)+"毫秒");

        long start2 = System.currentTimeMillis();
        int[] arr2 = seq(int[].class, size);
        buckert2(arr2);
        long end2 = System.currentTimeMillis();
        //System.out.println(Arrays.toString(arr2));
        System.out.println(size+"大小的桶排序+三路快排花费时间为:"+(end2-start2)+"毫秒");
    }

    /** 使用希尔*/
    private static void buckert(int[] arr) {
        // 获取最大值和最小值
        int maxValue = arr[0];
        int minValue = arr[0];
        for (int value : arr) {
            if (value>maxValue){
                maxValue = value;
            } else if (value < minValue){
                minValue = value;
            }
        }
        //计算桶的数量
        int bucketCount = (int)Math.floor((maxValue-minValue) / 6) + 1;
        // 定义二维数组
        int[][] buckets = new int[bucketCount][0];
        // 向桶中添加数据
        for (int i = 0; i < arr.length; i++) {
            int index = (int) Math.floor((arr[i] - minValue) / 6);
            buckets[index] = arrAppend(buckets[index], arr[i]);
        }
        // 遍历赋值
        int index = 0;
        for (int[] arr1 : buckets) {
            // 对每个桶元素排序,使用希尔排序或者归并,比较稳定
            shell(arr1);
            if (arr1.length!=0){
                for (int i = 0; i < arr1.length; i++) {
                    arr[index++] = arr1[i];
                }
            }
        }
    }
    /** 使用归并*/
    private static void buckert1(int[] arr) {
        // 获取最大值和最小值
        int maxValue = arr[0];
        int minValue = arr[0];
        for (int value : arr) {
            if (value>maxValue){
                maxValue = value;
            } else if (value < minValue){
                minValue = value;
            }
        }
        //计算桶的数量
        int bucketCount = (int)Math.floor((maxValue-minValue)/5) + 1;
        // 定义二维数组
        int[][] buckets = new int[bucketCount][0];
        // 向桶中添加数据
        for (int i = 0; i < arr.length; i++) {
            int index = (int) Math.floor((arr[i] - minValue) / 5);
            buckets[index] = arrAppend(buckets[index], arr[i]);
        }
        // 遍历赋值
        int index = 0;
        for (int[] arr1 : buckets) {
            // 对每个桶元素排序,使用希尔排序或者归并,比较稳定
            int[] tmp = new int[arr1.length];
            mergeSort(arr1,0,arr1.length-1,tmp);
            if (arr1.length!=0){
                for (int i = 0; i < arr1.length; i++) {
                    arr[index++] = arr1[i];
                }
            }
        }
    }
    /** 使用三路快排*/
    private static void buckert2(int[] arr) {
        // 获取最大值和最小值
        int maxValue = arr[0];
        int minValue = arr[0];
        for (int value : arr) {
            if (value>maxValue){
                maxValue = value;
            } else if (value < minValue){
                minValue = value;
            }
        }
        //计算桶的数量
        int bucketCount = (int)Math.floor((maxValue-minValue)/5) + 1;
        // 定义二维数组
        int[][] buckets = new int[bucketCount][0];
        // 向桶中添加数据
        for (int i = 0; i < arr.length; i++) {
            int index = (int) Math.floor((arr[i] - minValue) / 5);
            buckets[index] = arrAppend(buckets[index], arr[i]);
        }
        // 遍历赋值
        int index = 0;
        for (int[] arr1 : buckets) {
            // 对每个桶元素排序,使用希尔排序或者归并,比较稳定
            quickSort(arr1,0,arr1.length-1);
            if (arr1.length!=0){
                for (int i = 0; i < arr1.length; i++) {
                    arr[index++] = arr1[i];
                }
            }
        }
    }
    /** 希尔排序*/
    public static void shell(int[] arr){
        int length = arr.length;
        for (int step = length/2; step > 0; step=step/2) {
            for (int j = 0; j < length; j++) {
                int i = j;
                int tmp = arr[i];
                while(i+step<length && arr[i]>arr[i+step]){
                    //交换
                    arr[i+step] = arr[i];
                    i+=step;
                }
                arr[i] = tmp ;
            }
        }
    }

    /** 归并排序*/
    public static void mergeSort(int[] arr, int left, int right, int[] tmp){
        if (left < right) {
            // 计算中间值
            int middle = left + (right - left)/2;
            mergeSort(arr,left,middle,tmp);
            mergeSort(arr,middle+1,right,tmp);
            merge(arr,left,middle,right,tmp);
        }
    }

    private static void merge(int[] arr, int left, int middle, int right, int[] tmp) {
        int index = 0;
        int leftIndex = left;
        int rightIndex = middle+1;
        // 左右比大小
        while(leftIndex <= middle && rightIndex <= right){
            if (arr[leftIndex] > arr[rightIndex]) {
                tmp[index++] = arr[rightIndex++];
            } else {
                tmp[index++] = arr[leftIndex++];
            }
        }
        // 比较左边
        while(leftIndex<=middle){
            tmp[index++] = arr[leftIndex++];
        }
        // 比较右边
        while(rightIndex<=right){
            tmp[index++] = arr[rightIndex++];
        }
        System.arraycopy(tmp,0,arr,left,index);
    }

    /** 三路快速排序*/
    public static void quickSort(int[] arr, int left, int right){
        if (left < right) {
            int currentValue = arr[left];
            int rightIndex = right;
            int leftIndex = left+1;
            int i = left+1;
            while(i <= rightIndex) {
                if (arr[i] > currentValue) {
                    swap(arr,i,rightIndex);
                    rightIndex--;
                }else if (arr[i] < currentValue) {
                    swap(arr,i,leftIndex);
                    leftIndex++;
                    i++;
                } else {
                    i++;
                }
            }
            swap(arr, left, leftIndex-1);
            quickSort(arr,left,leftIndex-1);
            quickSort(arr,rightIndex+1,right);
        }
    }


    /** 自动扩容，并保存数据*/
    private static int[] arrAppend(int[] bucket, int value) {
        int[] copy = new int[bucket.length+1];
        System.arraycopy(bucket,0,copy,0,bucket.length);
        copy[bucket.length] = value;
        return copy;
    }


    public static void swap(int[] arr,int a,int b){
        if (a != b && arr[a] != arr[b]) {
            arr[a] = arr[a]+arr[b];
            arr[b] = arr[a]-arr[b];
            arr[a] = arr[a]-arr[b];
        }
    }


    @SuppressWarnings("unchecked")
    public static <T> T seq(Class<T> arrayClass, int size) {
        Class<?> componentType = arrayClass.getComponentType();
        String componentName = componentType.getName();
        T array = (T) Array.newInstance(componentType, size);
        Random random = new Random();
        if("char".equals(componentName) || Character.class.getName().equals(componentName)) {
            for (int i = 0; i < size; i++) {
                Array.set(array, i, (char)(random.nextInt(1000000)));
            }
        }else {
            for (int i = 0; i < size; i++) {
                Array.set(array, i, random.nextInt(1000000));
            }
        }
        return array;
    }
}
