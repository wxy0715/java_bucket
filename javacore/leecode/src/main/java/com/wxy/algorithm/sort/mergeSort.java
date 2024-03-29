package com.wxy.algorithm.sort;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

/**
 * https://www.runoob.com/w3cnote/merge-sort.html
 * 归并排序: 一分为二 最为合一 需要递归
 * 最好O(N*log2N)  平均O(N*log2N)  最坏O(N*log2N)
 *      1 2 3 4 5 6 7 8
 *    1 2 3 4     5 6 7 8
 * 1 2    3 4         5 6    7 8
 *1   2  3   4       5   6  7   8
 * */
public class mergeSort {
    public static int size = 1000000;
    public static int[] arr = {1,3,5,4,7,4,2,1};

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        sort(arr);
        long end = System.currentTimeMillis();
        System.out.println(Arrays.toString(arr));
        System.out.println(size+"大小的归并排序花费时间为:"+(end-start)+"毫秒");
    }
    public static void sort(int []arr){
        //在排序前，先建好一个长度等于原数组长度的临时数组，避免递归中频繁开辟空间
        int []temp = new int[arr.length];
        sort(arr,0,arr.length-1,temp);
    }
    private static void sort(int[] arr,int left,int right,int []temp){
        if(left<right){
            int mid = (left+right)/2;
            sort(arr,left,mid,temp);
            sort(arr,mid+1,right,temp);
            merge(arr,left,mid,right,temp);
        }
    }
    private static void merge(int[] arr,int left,int mid,int right,int[] temp){
        int i = left;
        int j = mid+1;
        int t = 0;
        while (i<=mid && j<=right){
            if(arr[i]<=arr[j]){
                temp[t++] = arr[i++];
            }else {
                temp[t++] = arr[j++];
            }
        }
        while(i<=mid){
            temp[t++] = arr[i++];
        }
        while(j<=right){
            temp[t++] = arr[j++];
        }
        System.arraycopy(temp,0,arr,left,t);
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
                Array.set(array, i, random.nextInt(1000000000));
            }
        }
        return array;
    }
}