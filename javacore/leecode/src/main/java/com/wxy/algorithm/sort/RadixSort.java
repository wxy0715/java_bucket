package com.wxy.algorithm.sort;

import java.lang.reflect.Array;
import java.util.Random;

/** *
 * 基数排序:https://www.runoob.com/w3cnote/radix-sort.html
 * 最好O(N*k) 平均O(N*k) 最坏O(N*k)
 * 先排序最低位,然后往高位排序
 */
public class RadixSort {

    public static int size = 100000;
    public static int[] arr = seq(int[].class, size);
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        int length = getMaxLength(arr);
        radixSort(arr,length);
        long end = System.currentTimeMillis();
        //System.out.println(Arrays.toString(arr));
        System.out.println(size+"大小的基数排序花费时间为:"+(end-start)+"毫秒");
    }

    private static void radixSort(int[] arr, int length) {
        int dev = 1;
        int mod = 10;
        // 定义二维数组
        for (int i = 0; i < length; i++,dev*=10,mod*=10) {
            // 考虑负数的情况，这里扩展一倍队列数，其中 [0-9]对应负数，[10-19]对应正数 (bucket + 10)
            int[][] tmp = new int[20][0];
            for (int j = 0; j < arr.length; j++) {
                int pos = (arr[j]%mod)/dev+10;
                tmp[pos] = appendValue(tmp[pos],arr[j]);
            }
            int index = 0;
            for (int[] a : tmp) {
                for (int b : a) {
                    arr[index++] = b;
                }
            }
        }
    }

    private static int[] appendValue(int[] arr, int value) {
        int[] tmp = new int[arr.length+1];
        System.arraycopy(arr,0,tmp,0,arr.length);
        tmp[tmp.length-1] = value;
        return tmp;
    }

    private static int getMaxLength(int[] arr) {
        int maxValue = getMaxValue(arr);
        int length = 1;
        while(maxValue/10 != 0) {
            maxValue /= 10;
            length++;
        }
        return length;
     }

    private static int getMaxValue(int[] arr) {
        int maxValue = 0;
        for (int value : arr) {
            if (value>maxValue) {
                maxValue = value;
            }
        }
        return maxValue;
    }

    @SuppressWarnings("unchecked")
    public static <T> T seq(Class<T> arrayClass, int size) {
        Class<?> componentType = arrayClass.getComponentType();
        String componentName = componentType.getName();
        T array = (T) Array.newInstance(componentType, size);
        Random random = new Random();
        if("char".equals(componentName) || Character.class.getName().equals(componentName)) {
            for (int i = 0; i < size; i++) {
                Array.set(array, i, (char)(random.nextInt(10000000)));
            }
        }else {
            for (int i = 0; i < size; i++) {
                Array.set(array, i, random.nextInt(10000000));
            }
        }
        return array;
    }

    public static void swap(int[] arr,int a,int b){
        if (a != b && arr[a] != arr[b]) {
            arr[a] = arr[a]+arr[b];
            arr[b] = arr[a]-arr[b];
            arr[a] = arr[a]-arr[b];
        }
    }
}
