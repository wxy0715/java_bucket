package com.wxy.utils;

import cn.hutool.core.text.StrFormatter;

import java.io.*;
import java.security.SecureRandom;
import java.util.*;

/**
 * 字符串工具类
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
    /** 空字符串 */
    private static final String NULLSTR = "";
    private static final String SEPARATOR = "_";

    public static final String LOW_STR = "abcdefghijklmnopqrstuvwxyz";
    public static final String SPECIAL_STR = "$@$!%*#_~?&";
    public static final String NUM_STR = "0123456789";

    /**
     * 拼串
     * @param strings
     */
    public static String mergeStr(String...strings) {
        StringBuilder sb = new StringBuilder();
        for (String str : strings) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * 获取指定个数数字字符串
     * @param num 个数
     */
    public static String getRanNum(Integer num) {
        StringBuilder str = new StringBuilder();
        for(int i=0;i<num;i++) {
            str.append((int) (Math.random() * 10));
        }
        return str.toString();
    }

    /**
     * 获取指定个数字符串
     * @param num 个数
     */
    public static String getRanStr(Integer num) {
        Random random = new Random();
        StringBuilder res = new StringBuilder();
        for(int i=0;i<num;i++) {
            int ranNum = random.nextInt(LOW_STR.length());
            res.append(LOW_STR.charAt(ranNum));
        }
        return res.toString();
    }

    /** 随机获取字符串字符 **/
    public static char getRandomChar(String str) {
        SecureRandom random = new SecureRandom();
        return str.charAt(random.nextInt(str.length()));
    }

    /**随机获取小写字符 **/
    public static char getLowChar() {
        return getRandomChar(LOW_STR);
    }

    /**随机获取大写字符 **/
    public static char getUpperChar() {
        return Character.toUpperCase(getLowChar());
    }

    /**随机获取数字字符 **/
    public static char getNumChar() {
        return getRandomChar(NUM_STR);
    }

    /** 随机获取特殊字符**/
    public static char getSpecialChar() {
        return getRandomChar(SPECIAL_STR);
    }

    /** 指定调用字符函数**/
    public static char getRandomChar(int funNum) {
        switch (funNum) {
            case 0:
                return getLowChar();
            case 1:
                return getUpperChar();
            case 2:
                return getNumChar();
            default:
                return getSpecialChar();
        }
    }

    /**指定长度，随机生成复杂密码 **/
    public String getRandomPwd(int num) {
        if (num > 32 || num < 8) {
            System.out.println("密码格式错误");
            return "";
        }
        List<Character> list = new ArrayList<>(num);
        list.add(getLowChar());
        list.add(getUpperChar());
        list.add(getNumChar());
        list.add(getSpecialChar());

        for (int i = 4; i < num; i++) {
            SecureRandom random = new SecureRandom();
            int funNum = random.nextInt(4);
            list.add(getRandomChar(funNum));
        }
        // 打乱排序
        Collections.shuffle(list);
        StringBuilder stringBuilder = new StringBuilder(list.size());
        for (Character c : list) {
            stringBuilder.append(c);
        }

        return stringBuilder.toString();
    }

    /**
     * 是否以指定字符串结尾，忽略大小写
     * @param str    被监测字符串
     * @param suffix 结尾字符串
     * @return 是否以指定字符串结尾
     */
    public static boolean endWithIgnoreCase(CharSequence str, CharSequence suffix) {
        return endWith(str, suffix, true);
    }

    /**
     * 字符串是否以给定字符结尾
     * @param str 字符串
     * @param c   字符
     * @return 是否结尾
     */
    public static boolean endWith(CharSequence str, char c) {
        return c == str.charAt(str.length() - 1);
    }

    /**
     * 是否以指定字符串结尾<br>
     * 如果给定的字符串和开头字符串都为null则返回true，否则任意一个值为null返回false
     * @param str          被监测字符串
     * @param suffix       结尾字符串
     * @param isIgnoreCase 是否忽略大小写
     * @return 是否以指定字符串结尾
     */
    public static boolean endWith(CharSequence str, CharSequence suffix, boolean isIgnoreCase) {
        if (null == str || null == suffix) {
            if (null == str && null == suffix) {
                return true;
            }
            return false;
        }
        if (isIgnoreCase) {
            return str.toString().toLowerCase().endsWith(suffix.toString().toLowerCase());
        } else {
            return str.toString().endsWith(suffix.toString());
        }
    }

    /**
     * 截取字符串
     * 
     * @param str 字符串
     * @param start 开始
     * @param end 结束
     * @return 结果
     */
    public static String substring(final String str, int start, int end)
    {
        if (str == null)
        {
            return NULLSTR;
        }

        if (end < 0)
        {
            end = str.length() + end;
        }
        if (start < 0)
        {
            start = str.length() + start;
        }

        if (end > str.length())
        {
            end = str.length();
        }

        if (start > end)
        {
            return NULLSTR;
        }

        if (start < 0)
        {
            start = 0;
        }
        if (end < 0)
        {
            end = 0;
        }

        return str.substring(start, end);
    }

    /**
     * 格式化文本, {} 表示占位符<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") -> this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") -> this is \{} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") -> this is \a for b<br>
     * 
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param params 参数值
     * @return 格式化后的文本
     */
    public static String format(String template, Object... params) {
        if (isBlank(template)) {
            return template;
        }
        return StrFormatter.format(template, params);
    }

    /**
     * 是否包含字符串
     * 
     * @param str 验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inStringIgnoreCase(String str, String... strs) {
        if (str != null && strs != null) {
            for (String s : strs) {
                if (str.equalsIgnoreCase(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 驼峰转下划线命名
     */
    public static String toUnderScoreCase(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        // 前置字符是否大写
        boolean preCharIsUpperCase = true;
        // 当前字符是否大写
        boolean curreCharIsUpperCase = true;
        // 下一字符是否大写
        boolean nexteCharIsUpperCase = true;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i > 0) {
                preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
            } else {
                preCharIsUpperCase = false;
            }

            curreCharIsUpperCase = Character.isUpperCase(c);

            if (i < (str.length() - 1)) {
                nexteCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
            }

            if (preCharIsUpperCase && curreCharIsUpperCase && !nexteCharIsUpperCase) {
                sb.append(SEPARATOR);
            } else if ((i != 0 && !preCharIsUpperCase) && curreCharIsUpperCase) {
                sb.append(SEPARATOR);
            }
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /** 深拷贝**/
    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

    /** 字符数组追加**/
    public static String[] insert(String[] arr, String... str) {
        int size = arr.length; // 获取原数组长度
        int newSize = size + str.length; // 原数组长度加上追加的数据的总长度

        // 新建临时字符串数组
        String[] tmp = new String[newSize];
        // 先遍历将原来的字符串数组数据添加到临时字符串数组
        System.arraycopy(arr, 0, tmp, 0, size);
        // 在末尾添加上需要追加的数据
        if (newSize - size >= 0) {
            System.arraycopy(str, 0, tmp, size, newSize - size);
        }
        return tmp; // 返回拼接完成的字符串数组
    }
}