package com.wxy.utils.date;

import com.wxy.utils.date.base.DateOpt;

/**
 * 日期工具
 * 提供了 javax.time.ZoneId 获取时区。
 * 提供了 LocalDate 和 LocalTime 类。
 * Java 8 的所有日期和时间 API 都是不可变类并且线程安全，而现有的 Date 和 Calendar API 中的 java.util.Date 和SimpleDateFormat 是非线程安全的。
 * 主包是 java.time, 包含了表示日期、时间、时间间隔的一些类。里面有两个子包 java.time.format 用于格式化， java.time.temporal 用于更底层的操作。
 * 时区代表了地球上某个区域内普遍使用的标准时间。每个时区都有一个代号，格式通常由区域/城市构成（Asia/Tokyo），在加上与格林威治或 UTC 的时差。例如：东京的时差是 +09:00。
 * OffsetDateTime 类实际上组合了 LocalDateTime 类和 ZoneOffset 类。用来表示包含和格林威治或 UTC 时差的完整日期（年、月、日）和时间（时、分、秒、纳秒）信息。
 * DateTimeFormatter 类用来格式化和解析时间。与 SimpleDateFormat 不同，这个类不可变并且线程安全，需要时可以给静态常量赋值。 
 * DateTimeFormatter 类提供了大量的内置格式化工具，同时也允许你自定义。在转换方面也提供了 parse() 将字符串解析成日期，如果解析出错会抛出 DateTimeParseException。
 * DateTimeFormatter 类同时还有format() 用来格式化日期，如果出错会抛出 DateTimeException异常。
 * @author wangxingyu
 * @date 2023/02/22
 */
public class DateTool extends DateOpt {
 
     /** 面试题 1.SimpleDateFormat非线程安全
        SimpleDateFormat继承了DateFormat,DateFormat类中维护了一个全局的Calendar变量
        sdf.parse(dateStr)和sdf.format(date)，都是由Calendar引用来储存的。
        如果SimpleDateFormat是static全局共享的，Calendar引用也会被共享。
        又因为Calendar内部并没有线程安全机制，所以全局共享的SimpleDateFormat不是线性安全的。

        解决SimpleDateFormat线性不安全问题，有三种方式：
        1.将SimpleDateFormat定义为局部变量 2.使用ThreadLocal 3.方法加同步锁synchronized
      */
 
     /** 时区操作
        LocalDateTime now = LocalDateTime.now();
        // 获取所有时区信息
        Set<String> availableZoneIds = ZoneId.getAvailableZoneIds();
        // 获取系统时区
        ZoneId zoneId = ZoneId.systemDefault();
        // 添加时区
        ZonedDateTime zonedDateTime = now.atZone(ZoneId.of("Asia/Shanghai"));
        // 更改时区
        ZonedDateTime zonedDateTime1 = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Tokyo"));
      */
 
     /** 时间操作
        //LocalTime plusNanos(long nanos) 增加纳秒
        //LocalTime plusSeconds(long seconds) 增加秒
        //LocalTime plusMinutes(long minutes) 增加分钟
        //LocalTime plusHours(long hours) 增加小时
        //LocalDateTime withNano(int i) 修改纳秒
        //LocalDateTime withSecond(int i) 修改秒
        //LocalDateTime withMinute(int i) 修改分支
        //LocalDateTime withHour(int i) 修改小时
        //LocalDateTime withDayOfMonth(int i) 修改日
        //LocalDateTime withMonth(int i) 修改月
        //LocalDateTime withYear(int i) 修改年
        //with(TemporalField field, long newValue)
        //with(ChronoField.DAY_OFMONTH,1);将日期中的月份中的天数改为1
        //with(ChronoField.YEAR,2021);将日期中的年份改为2021。
        // 修改时间为当月第一天
        LocalDate firstDayOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        // 改为下一个星期三
        LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));
      */
 

}
