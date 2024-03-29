package com.wxy.leecode.差分数组;

import java.util.Arrays;

/**
 * 差分数组类
 * nums 8  2 6  3  1
 * diff 8 -6 4 -3 -2
 */
public class Difference {
    // 差分数组
    private int[] diff;

    /* 输入一个初始数组，区间操作将在这个数组上进行 */
    public Difference(int[] nums){
        assert nums.length > 0;
        diff = new int[nums.length];
        diff[0] = nums[0];
        for (int i = 1; i < nums.length; i++) {
            diff[i] = nums[i] - nums[i-1];
        }
    }


    /**
     * 给闭区间 [i,j] 增加 val（可以是负数）
     */
    public void increment(int i, int j, int val) {
        diff[i] += val;
        if (j+1 < diff.length) {
            diff[j+1] -= val;
        }
    }

    /**
     * 返回结果数组
     */
    public int[] result() {
        int[] nums = new int[diff.length];
        nums[0] = diff[0];
        for (int i = 1; i < diff.length; i++) {
            nums[i] = nums[i-1] + diff[i];
        }
        return nums;
    }

    public static void main(String[] args) {
        int[] ints = {8,2,6,3,1};
        Difference difference = new Difference(ints);
        System.out.println(Arrays.toString(ints));
        System.out.println(Arrays.toString(difference.diff));
        difference.increment(1,2,3);
        System.out.println(Arrays.toString(difference.result()));
    }
}
