package com.cjree.core.basic.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Description：匹配最相似的字符串
 */
public class MatchStringValueUtil {

    public static String match(List<String> strA,  String strB){
        HashMap<Double, String> map = new HashMap<>();
        for (String s : strA) {
            double v = matchString(s, strB);
            map.put(v,s);
        }
        //获取值最大的键
        Double maxKey = Collections.max(map.keySet());
        //找到该键对应的值
        return map.get(maxKey);
    }
    // strA是需要匹配的数据  strB是传入的数据 wxy明白了吗
    public static double matchString(String strA, String strB) {
        // 调用的时候比较这个值 谁大就应用谁
        return 1 - (double) compare(strA, strB) / Math.max(strA.length(), strB.length());
    }
    private static int compare(String str, String target) {
        int[][] d; // 矩阵
        int n = str.length();
        int m = target.length();
        int i; // 遍历str的
        int j; // 遍历target的
        char ch1; // str的
        char ch2; // target的
        int temp; // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        d = new int[n + 1][m + 1];
        // 初始化第一列
        for (i = 0; i <= n; i++) {
            d[i][0] = i;
        }
        // 初始化第一行
        for (j = 0; j <= m; j++) {
            d[0][j] = j;
        }
        // 遍历str
        for (i = 1; i <= n; i++) {
            ch1 = str.charAt(i - 1);
            // 去匹配target
            for (j = 1; j <= m; j++) {
                ch2 = target.charAt(j - 1);
                if (ch1 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }

                // 左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
            }
        }
        return d[n][m];
    }

    private static int min(int one, int two, int three) {
        return (one = Math.min(one, two)) < three ? one : three;
    }
}
