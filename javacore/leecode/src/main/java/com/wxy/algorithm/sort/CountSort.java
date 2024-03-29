package com.wxy.algorithm.sort;

/**
 * 计数排序法:最好O(n+k) 平均O(n+k) 最坏O(n+k)
 */
public class CountSort {
    private static int[] countingSort(int[] arr, int maxValue) {
        int bucketLen = maxValue + 1;
        int[] bucket = new int[bucketLen];

        for (int value : arr) {
            bucket[value]++;
        }

        int sortedIndex = 0;
        for (int j = 0; j < bucketLen; j++) {
            while (bucket[j] > 0) {
                arr[sortedIndex++] = j;
                bucket[j]--;
            }
        }
        return arr;
    }

    private static int getMaxValue(int[] arr) {
        int maxValue = arr[0];
        for (int value : arr) {
            if (maxValue < value) {
                maxValue = value;
            }
        }
        return maxValue;
    }

    public static void main(String[] args) {
        int[] theArray = {6, 4, 5, 1, 8, 7, 2, 3, 6};
        System.out.print("之前的排序：");
        for (int i = 0; i < theArray.length; i++) {
            System.out.print(theArray[i] + " ");
        }

        int maxValue = getMaxValue(theArray);

        int[] ints = countingSort(theArray, maxValue);

        System.out.print("计数排序：");
        for (int i = 0; i < ints.length; i++) {
            System.out.print(ints[i] + " ");
        }
    }
}
