package com.wxy.leecode.前缀和;

/**
 *  二维区域和检索 - 矩阵不可变
 */
public class Leecode304 {
    private int[][] preSum;

    // 构造前缀和矩阵
    public Leecode304(int[][] matrix) {
        // 分别获取x轴和y轴的长度
        int x = matrix.length;
        int y = matrix[0].length;
        if (y == 0) {
            return;
        }
        // 初始化容量
        preSum = new int[x+1][y+1];
        for(int i = 1; i <= x; i++) {
            for(int j = 1; j <= y; j++) {
                // 某个点的值是(x轴+y轴)-(x-1,y-1)轴+自身
                preSum[i][j] = preSum[i-1][j] + preSum[i][j-1] + matrix[i - 1][j - 1] - preSum[i-1][j-1];
            }
        }
    }

    //    x1,y1, x2,y1
    //    x1,y2  x2,y2
    public int sumRegion(int x1, int y1, int x2, int y2) {
        return preSum[x2+1][y2+1] - preSum[x2+1][y1] - preSum[x1][y2+1] + preSum[x1][y1];
    }
}