package com.wxy.leecode.前缀和;

/**
 * 区域和检索 - 数组不可变
 */
public class Leecode303 {
    // 定义前缀和新数组
    int[] preSum;

    // 构造前缀和新数组
    public Leecode303(int[] nums) {
        preSum = new int[nums.length + 1];
        for (int i = 1; i < preSum.length; i++) {
            preSum[i] = preSum[i - 1] + nums[i - 1];
        }
    }
    public int sumRange(int left, int right) {
        return preSum[right + 1] - preSum[left];
    }

    public static void main(String[] args) {
        int[] arr = new int[3];
        arr[0] = 1;
        arr[1] = 2;
        arr[2] = 3;
        Leecode303 leecode303 = new Leecode303(arr);
        System.out.println(leecode303.sumRange(1,2));
    }
}

//     数组   [1,2,3,4]
//前缀和数组 [0,1,3,6,10]