package com.wxy.utils.date.base;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Date;

/**
 * 时间加减计算
 *
 * 如何用 Java 判断日期是早于还是晚于另一个日期
 * 计算两个日期之间的天数和月数
 * 在 Java 8 中获取当前的时间戳 Instant timestamp = Instant.now();
 * @author wangxingyu
 * @date 2023/02/22
 */
public abstract class DateOpt extends DateGet{

    /**
     * 间隔:
     * duration.toDays();
     * duration.toHours();
     * duration.toMinutes();
     */
    public static Duration getDurationBetween(Date startDate, Date endDate) {
        LocalDateTime start = dateConvertToLocalDateTime(startDate);
        LocalDateTime end = dateConvertToLocalDateTime(endDate);
        Duration duration = Duration.between(start,end);
        return duration;
    }

    /**
     * 间隔:
     * Period.getDays()
     * period2.getYears();
     * period2.getMonths();
     * period2.toTotalMonths();
     */
    public static Period getBetween(Date startDate, Date endDate) {
        LocalDate start = dateConvertToLocalDate(startDate);
        LocalDate end = dateConvertToLocalDate(endDate);
        return Period.between(start, end);
    }

    /**
     * 时间比较大小
     */
    public static int dateCompareTo(Date left, Date right) {
        return right.compareTo(left);
    }

    /**
     * 比较两个日期是否是同年同月同日
     */
    public static boolean dateCompareByYearMonthDay(Date left, Date right) {
        LocalDate localDate = dateConvertToLocalDate(left);
        LocalDate localDate1 = dateConvertToLocalDate(right);
        return localDate.compareTo(localDate1) == 0;
    }

    /**
     * 指定日期添加天数
     */
    public static LocalDate addDate(LocalDate localDate,int day){
        return localDate.plusDays(day);
    }
    public static LocalDate addDate(int day){
        return LocalDate.now().plusDays(day);
    }
    /**
     * 指定日期减少天数
     */
    public static LocalDate minusDate(LocalDate localDate,int day){
        return localDate.minusDays(day);
    }
    public static LocalDate minusDate(int day){
        return LocalDate.now().minusDays(day);
    }

    /**
     * 指定日期添加月份
     */
    public static LocalDate addMonth(LocalDate localDate,int month){
        return localDate.plusMonths(month);
    }
    public static LocalDate addMonth(int month){
        return LocalDate.now().plusMonths(month);
    }
    /**
     * 指定日期减少月份
     */
    public static LocalDate minusMonth(LocalDate localDate,int month){
        return localDate.minusMonths(month);
    }
    public static LocalDate minusMonth(int month){
        return LocalDate.now().minusMonths(month);
    }

    /**
     * 指定日期添加周
     */
    public static LocalDate addWeek(LocalDate localDate,int week){
        return localDate.plusWeeks(week);
    }
    public static LocalDate addWeek(int week){
        return LocalDate.now().plusWeeks(week);
    }
    /**
     * 指定日期减少周
     */
    public static LocalDate minusWeek(LocalDate localDate,int week){
        return localDate.minusWeeks(week);
    }
    public static LocalDate minusWeek(int week){
        return LocalDate.now().minusWeeks(week);
    }

    /**
     * 指定日期添加年
     */
    public static LocalDate addYear(LocalDate localDate,int year){
        return localDate.plusYears(year);
    }
    public static LocalDate addYear(int year){
        return LocalDate.now().plusYears(year);
    }
    /**
     * 指定日期减少年
     */
    public static LocalDate minusYear(LocalDate localDate,int year){
        return localDate.minusYears(year);
    }
    public static LocalDate minusYear(int year){
        return LocalDate.now().minusYears(year);
    }

}
