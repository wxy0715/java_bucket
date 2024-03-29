package com.wxy.algorithm.search;

import org.junit.Test;

public class search {

    /** 线性查找法*/
    @Test
    public void linearSearch(){
        int[] data = {1,12,21143,41414,41515,166,62626};
        int target = 166;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == target) {
                System.out.println(i);
                break;
            }
        }
    }


}
