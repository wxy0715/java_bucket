package com.wxy.algorithm.sort;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.Random;
/**https://www.cnblogs.com/fengff/p/10983733.html
 * https://www.runoob.com/data-structures/data-structures-tutorial.html 数据结构与算法
 * https://www.runoob.com/w3cnote/ten-sorting-algorithm.html 十大经典排序算法
 * */
public class Sort {
    public static int size = 1000000;
    public static int[] arr = seq(int[].class, size);
    /**
     * https://www.runoob.com/w3cnote/bubble-sort.html
     * 冒泡排序:最好O(N) 平均O(N2) 最坏O(N2)
     * 比较相邻的元素。如果第一个比第二个大，就交换他们两个。
     * 对每一对相邻元素作同样的工作，从开始第一对到结尾的最后一对。这步做完后，最后的元素会是最大的数。
     * 针对所有的元素重复以上的步骤，除了最后一个。
     * 持续每次对越来越少的元素重复上面的步骤，直到没有任何一对数字需要比较。
     * */
    @Test
    public void bubble(){
        long start = System.currentTimeMillis();
        for (int i = 1; i < size; i++) {
            // 设定一个标记，若为true，则表示此次循环没有进行交换，也就是待排序列已经有序，排序已经完成。
            boolean flag = true;
            for (int j = 0; j < size-i; j++) {
                if (arr[j] > arr[j+1]) {
                    int temp = arr[j+1];
                    arr[j+1] = arr[j];
                    arr[j] = temp;
                    flag = false;
                }
            }
            if (flag) {
                break;
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(size+"大小的冒泡排序花费时间为:"+(end-start));
    }
    
    /**
     * https://www.runoob.com/w3cnote/selection-sort.html
     * 选择排序法:最好O(N) 平均O(N2) 最坏O(N2)
     * 首先在未排序序列中找到最小（大）元素，存放到排序序列的起始位置。
     * 再从剩余未排序元素中继续寻找最小（大）元素，然后放到已排序序列的末尾。
     * 重复第二步，直到所有元素均排序完毕。
     * */
    @Test
    public void selection(){
        long start = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            int min = i;
            for (int j = i+1; j < size; j++) {
                if (arr[min] > arr[j]) {
                    min = j;
                }
            }
            if (min != i) {
                int temp = arr[i];
                arr[i] = arr[min];
                arr[min] = temp;
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(size+"大小的选择排序花费时间为:"+(end-start));
    }

    /**
     * https://www.runoob.com/w3cnote/insertion-sort.html
     * 插入排序:最好O(N)平均O(N2) 最坏O(N2)
     * 将第一待排序序列第一个元素看做一个有序序列，把第二个元素到最后一个元素当成是未排序序列。
     * 从头到尾依次扫描未排序序列，将扫描到的每个元素插入有序序列的适当位置。
     *（如果待插入的元素与有序序列中的某个元素相等，则将待插入元素插入到相等元素的后面。）
     * */
    @Test
    public void insertion(){
        long start = System.currentTimeMillis();
        for (int i = 1; i < size; i++) {
            int tmp = arr[i];
            // 逆序找到已排序的比当前元素大的数,后移
            // 12 14 16 13
            // 12 14 * 16
            // 12 * 14 16
            // 12 13 14 16
            int j = i;
            while (j>0 && tmp < arr[j-1]) {
                arr[j] = arr[j-1];
                j--;
            }
            if (i != j) {
             arr[j] = tmp;
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(size+"大小的插入排序花费时间为:"+(end-start));
    }

    /**
     * https://www.runoob.com/w3cnote/shell-sort.html
     * https://www.cnblogs.com/chengxiao/p/6104371.html
     * 希尔排序:最好O(N) 平均O(N1.3) 最坏O(N2)
     * 选择一个增量序列 t1，t2，……，tk，其中 ti > tj, tk = 1；
     * 按增量序列个数 k，对序列进行 k 趟排序；
     * 每趟排序，根据对应的增量 ti，将待排序列分割成若干长度为 m 的子序列，分别对各子表进行直接插入排序。仅增量因子为 1 时，整个序列作为一个表来处理，表长度即为整个序列的长度。
     * */
    @Test
    public void shellSort(){
        long start = System.currentTimeMillis();
        for (int step = size / 2; step > 0; step /= 2) {
            for (int i = step; i < size; i++) {
                // 交换法
 /*               int j = i;
                while(j-step >= 0 && arr[j] < arr[j-step]){
                    swap(arr,j,j-step);
                    j-=step;
                }*/
                // 移动法
                int j = i;
                int tmp = arr[j];
                if (arr[j] < arr[j-step]) {
                    while(j-step>=0 && tmp<arr[j-step]) {
                        arr[j] = arr[j-step];
                        j-=step;
                    }
                    arr[j] = tmp;
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(size+"大小的希尔排序花费时间为:"+(end-start));
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

     /**
     * 交换数组元素
     * @param arr
     * @param a
     * @param b
     */
     public static void swap(Integer[] arr,int a,int b){
       arr[a] = arr[a]+arr[b];
       arr[b] = arr[a]-arr[b];
       arr[a] = arr[a]-arr[b];
    }
}