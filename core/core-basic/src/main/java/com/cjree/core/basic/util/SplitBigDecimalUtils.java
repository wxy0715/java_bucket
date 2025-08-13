package com.cjree.core.basic.util;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class SplitBigDecimalUtils {

    /**
     * 每个红包最小金额，单位为与scale有关,例如scale为2,单位为分
     */
    private static final int MIN_MONEY = 1;

    /**
     * 红包金额的离散程度，值越大红包金额越分散
     */
    private static final double DISPERSE = 1;

    private static final BigDecimal pow = new BigDecimal(10);

    /**
     * 将一个BigDecimal 切分成指定个数指定精度指定范围的若干个数,如果范围设置不当,前面随机的数较小,可能会导致最后一个数过大超出范围
     * @param amount 总数量
     * @param count 总个数
     * @param scale 精度
     * @param min 最大值
     * @param max 最小值
     * @return
     */
    public static List<BigDecimal> splitBigDecimalFromRange(BigDecimal amount, int count, int scale, BigDecimal min, BigDecimal max) {
        BigDecimal total = new BigDecimal(0);
        List<BigDecimal> list = Lists.newArrayList();
        for (int i = 0; i < count; i++) {
            BigDecimal tem = getOneRedBag(amount.subtract(total), count - i, scale, min, max);
            total = total.add(tem);
            list.add(tem);
        }
        return list;
    }

    public static void main(String[] args) {
        //总量
        BigDecimal amount = new BigDecimal(16);
        BigDecimal min = BigDecimal.ZERO;
        BigDecimal max = amount.subtract(min);

        List<BigDecimal> bigDecimals = splitBigDecimalFromRange(amount, 1, 2, min, max);
        System.out.println(bigDecimals);
    }



    private static BigDecimal getOneRedBag(BigDecimal amount, int count, int scale, BigDecimal min, BigDecimal max) {
        // 转成int做运算
        int amountInt = amount.setScale(scale, RoundingMode.HALF_UP).multiply(pow).intValue();
        int minInt = min.setScale(scale, RoundingMode.HALF_UP).multiply(pow).intValue();
        int maxInt = max.setScale(scale, RoundingMode.HALF_UP).multiply(pow).intValue();
        if (amountInt < minInt * count) {
            throw new RuntimeException("最小值设置过大");
        }
        if (minInt > maxInt){
            throw new RuntimeException("最大值小于最小值");
        }
        if (maxInt * count < amountInt){
            throw new RuntimeException("最大值设置过小");
        }
        //最大值不能大于总金额
        maxInt = maxInt > amountInt ? amountInt : maxInt;
        //randomBetweenMinAndMax()上面有这个方法
        return new BigDecimal(randomBetweenMinAndMax(amountInt, count, minInt, maxInt)).divide(pow, scale,
                RoundingMode.HALF_UP);
    }

    /**
     * 在最小值和最大值之间随机产生一个
     *
     * @param money
     * @param count
     * @param min   : 最小量
     * @param max   ： 最大量
     * @return
     */
    public static int randomBetweenMinAndMax(int money, int count, int min, int max) {
        //最后一个直接返回
        if (count == 1) {
            return money;
        }
        //最小和最大金额一样，返最小和最大值都行
        if (min == max) {
            return min;
        }
        //最小值 == 均值， 直接返回最小值
        if (min == money / count) {
            return min;
        }
        //min<=随机数bag<=max
        int bag = ((int) Math.rint(Math.random() * (max - min) + min));

        //剩余的均值
        int avg = (money - bag) / (count - 1);
        //比较验证剩余的还够不够分(均值>=最小值 是必须条件),不够分的话就是最大值过大
        if (avg < MIN_MONEY) {
            /*
             * 重新随机一个，最大值改成本次生成的量
             * 由于 min<=本次金额bag<=max, 所以递归时bag是不断减小的。
             * bag在减小到min之间一定有一个值是合适的，递归结束。
             * bag减小到和min相等时，递归也会结束，所以这里不会死递归。
             */
            return randomBetweenMinAndMax(money, count, min, bag);
        } else {
            return bag;
        }
    }
}

