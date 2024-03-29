package com.wxy.leecode.前缀和;

import java.util.HashMap;
import java.util.Map;

/**
 * 和为 K 的子数组
 */
public class Leecode506 {
    public int subarraySum(int[] nums, int k) {
        Map<Integer,Integer> map = new HashMap<>(16);
        map.put(0,1);
        int res = 0;
        int len = nums.length;
        int sum = 0;
        for(int i=0; i<len;i++) {
            sum += nums[i];
            // 找到满足条件的节点
            int result = sum - k;
            if(map.containsKey(result)) {
                res += map.get(result);
            }
            map.put(sum, map.getOrDefault(sum, 0) + 1);
        }
        return res;
    }
}