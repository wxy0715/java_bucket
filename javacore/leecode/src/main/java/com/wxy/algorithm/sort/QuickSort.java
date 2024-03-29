package com.wxy.algorithm.sort;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

/**
 * https://www.runoob.com/w3cnote/quick-sort-2.html
 * 快速排序:最好O(N*log2N) 平均O(N*log2N) 最坏O(N2)
 * 从数列中挑出一个元素，称为 "基准"（pivot）;
 * 重新排序数列，所有元素比基准值小的摆放在基准前面，所有元素比基准值大的摆在基准的后面（相同的数可以到任一边）。
 * 在这个分区退出之后，该基准就处于数列的中间位置。这个称为分区（partition）操作；
 * 递归地（recursive）把小于基准值元素的子数列和大于基准值元素的子数列排序；
 * */
/**
index = 1;
pivot = 0;
2 1 3 4 0 -1 2 index = 2 i=1
2 1 3 4 0 -1 2 index = 2 i=2
2 1 3 4 0 -1 2 index = 2  i=3
2 1 0 4 3 -1 2 index = 3  i=4
2 1 0 -1 3 4 2 index = 4  i=5
2 1 0 -1 2 4 3 index = 5  i =6
2 1 0 -1 2 4 3 4和0交换
*/
public class QuickSort {
    public static int size = 100;
    public static long[] arr = seq(long[].class, size);
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        sort3(arr,0, arr.length - 1);
        long end = System.currentTimeMillis();
        System.out.println(Arrays.toString(arr));
        System.out.println(size+"大小的快速排序花费时间为:"+(end-start)+"毫秒");
    }

    /** 单路快排,都是相等的或者递增的效率比较慢*/
    private static void sort(long[] arr, int left, int right) {
        if (left < right) {
            int partition = partition(arr,left,right);
            sort(arr,left,partition-1);
            sort(arr,partition+1,right);
        }
    }

    private static int partition(long[] arr, int left, int right) {
        int index = left + 1;
        for (int i = index; i <= right; i++) {
            if (arr[i] < arr[left]) {
                swap(arr, i, index);
                index++;
            }
        }
        swap(arr, left, index - 1);
        return index - 1;
    }

    /** 三路快排,都是相等的或者递增的效率比较慢*/
    private static void sort3(long[] arr, int left, int right) {
        if (left < right) {
            long v = arr[left];
            int lt = left+1;
            int gt = right;
            int i = left + 1;
            while (i <= gt) {
                if (arr[i] < v) {
                    swap(arr, lt, i);
                    i++;
                    lt++;
                } else if (arr[i] > v) {
                    swap(arr, gt, i);
                    gt--;
                } else {
                    i++;
                }
            }
            swap(arr, left, lt-1);
            sort3(arr,left,lt-1);
            sort3(arr,gt+1,right);
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
                Array.set(array, i, (char)(random.nextInt(1000000000)));
            }
        }else {
            for (int i = 0; i < size; i++) {
                Array.set(array, i, random.nextInt(1000000));
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
