package com.wxy.util;

import java.util.Random;

public class GenPwdUtil {
    private static final String ASCII_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String ASCII_UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#%&*+=";
    private static final String CHAR1 = "!@#%&*+=";
    private static final String NUMBER = "0123456789";

    /**
     * @author wxy
     * @description  生成指定位数的密码 ,len最小为4
     * @create 2021/10/28 15:14
     */
    public static String generatorPwdByLen(int len){
        char one = ASCII_LOWERCASE.charAt(new Random().nextInt(ASCII_LOWERCASE.length()));
        char two = ASCII_UPPERCASE.charAt(new Random().nextInt(ASCII_UPPERCASE.length()));
        char three = NUMBER.charAt(new Random().nextInt(NUMBER.length()));
        char four = CHAR1.charAt(new Random().nextInt(CHAR1.length()));
        StringBuilder password = new StringBuilder();
        password.append(one).append(two).append(three).append(four);
        if (len < 5) {
            // 或者抛出异常
            return password.toString();
        }
        len = len - password.length();
        for (int i = 0; i < len; i++) {
            password.append(CHAR.charAt(new Random().nextInt(CHAR.length())));
        }
        return password.toString();
    }

    public static void main(String[] args) {
        System.out.println(generatorPwdByLen(8));
    }
}
