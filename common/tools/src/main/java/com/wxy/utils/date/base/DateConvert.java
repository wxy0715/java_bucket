package com.wxy.utils.date.base;

import com.wxy.utils.date.enums.DateStyle;
import org.springframework.util.ObjectUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间转换
 *
 * @author wangxingyu
 * @date 2023/02/22
 */
public abstract class DateConvert {
    protected static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 将java.util.Date 转换为java8 的java.time.LocalDateTime,默认时区为东8区
     */
    public static LocalDateTime dateConvertToLocalDateTimeUTC(Date date) {
        return date.toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
    }

    public static LocalDateTime dateConvertToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(),ZoneId.systemDefault());
    }

    /**
     * 将java.util.Date 转换为java8 的java.time.LocalDate,默认时区为东8区
     */
    public static LocalDate dateConvertToLocalDateUTC(Date date) {
        return date.toInstant().atOffset(ZoneOffset.of("+8")).toLocalDate();
    }
    public static LocalDate dateConvertToLocalDate(Date date) {
        return date.toInstant().atOffset(ZoneOffset.of("+0")).toLocalDate();
    }

    /**
     * 将java8 的 java.time.LocalDateTime 转换为 java.util.Date，默认时区为东8区
     */
    public static Date localDateTimeConvertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.toInstant(ZoneOffset.of("+8")));
    }

    /**
     * 字符串转Date
     * @param date 日期字符串 格式为yyyy-MM-dd HH:mm:ss
     * @return 日期
     */
    public static Date stringToDate(String date) throws ParseException {
        return stringToDate(date,null);
    }

    /**
     * 字符串转毫秒
     */
    public static long stringToDateLong(String dateStr,String format) throws ParseException {
        Date date = stringToDate(dateStr,format);
        return date == null ? 0L : date.getTime();
    }

    /**
     * 字符串转Date
     * @param date 日期字符串 格式为yyyy-MM-dd HH:mm:ss
     * @return 日期
     */
    public static Date stringToDate(String date,String format) throws ParseException {
        if(ObjectUtils.isEmpty(format)) {
            format = DateStyle.YYYY_MM_DD_HH_MM_SS.getValue();
        }
        return getDateByString(date, format);
    }

    /**
     * 字符串转Date
     * @param date 日期字符串 格式为yyyy-MM-dd HH:mm:ss
     * @return 日期
     */
    public static String dateToString(Date date) {
        return dateToString(date,null);
    }

    /**
     * 将日期转化为日期字符串。失败返回null。
     * @param date 日期
     * @param pattern 日期格式
     * @return 日期字符串
     */
    public static String dateToString(Date date, String pattern) {
        if ("".equalsIgnoreCase(pattern)){
            pattern = DateStyle.YYYY_MM_DD_HH_MM_SS.getValue();
        }
        String finalPattern = pattern;
        final ThreadLocal<DateFormat> df = ThreadLocal.withInitial(() -> new SimpleDateFormat(finalPattern));
        DateFormat dateFormat = df.get();
        return dateFormat.format(date);
    }

    /**
     * 把时间转为自定义格式-线程安全
     * @param time 毫秒或秒
     * @param param 自定义格式
     * */
    public static String dateFormatByCustomize(String param,long time) {
        final ThreadLocal<DateFormat> df = ThreadLocal.withInitial(() -> new SimpleDateFormat(param));
        DateFormat dateFormat = df.get();
        return dateFormat.format(time);
    }

    /**
     * 把时间转为自定义格式-线程安全
     * @param param 自定义格式
     * @param time 时间
     * */
    public static long getMilliTimeByDate(String param,String time) throws ParseException {
        final ThreadLocal<DateFormat> df = ThreadLocal.withInitial(() -> new SimpleDateFormat(param));
        DateFormat dateFormat = df.get();
        return dateFormat.parse(time).getTime();
    }

    /**
     * 把时间转为自定义格式-线程安全
     * @param param 自定义格式
     * @param time 时间
     * */
    public static Date getDateByString(String time,String param) throws ParseException {
        final ThreadLocal<DateFormat> df = ThreadLocal.withInitial(() -> new SimpleDateFormat(param));
        DateFormat dateFormat = df.get();
        return dateFormat.parse(time);
    }

}
