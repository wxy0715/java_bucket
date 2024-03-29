package com.wxy.utils.date.base;

import com.wxy.utils.date.enums.DateStyle;
import com.wxy.utils.date.enums.Week;
import org.apache.commons.lang3.RandomUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 获取日期
 *
 * @author wangxingyu
 * @date 2023/02/22
 */
public abstract class DateGet extends DateConvert{
    /**
     * 判断是否是闰年
     */
    public static boolean isLeapYear() {
        return LocalDate.now().isLeapYear();
    }

    /**
     * 获取当前时间
     */
    public static Date getCurrentDate(){
        return new Date();
    }
    public static LocalDateTime getCurrentLocalDateTime(){
        return LocalDateTime.now();
    }
    public static LocalDate getCurrentLocalDate(){
        return LocalDate.now();
    }
    public static LocalTime getCurrentLocalTime(){
        return LocalTime.now();
    }

    /**获取年*/
    public static int getYear(){
        LocalDate localDate = LocalDate.now();;
        return localDate.getYear();
    }

    /**获取年月*/
    public static YearMonth getYearMonth(){
        YearMonth yearMonth = YearMonth.now();
        return yearMonth;
    }

    /**获取月份*/
    public static int getMonth(){
        LocalDate localDate = LocalDate.now();;
        return localDate.getMonthValue();
    }

    /**获取月日*/
    public static MonthDay getMonthDay(){
        MonthDay monthDay = MonthDay.now();
        return monthDay;
    }

    /**获取日*/
    public static int getDay(){
        LocalDate localDate = LocalDate.now();;
        return localDate.getDayOfMonth();
    }

    /**获取小时*/
    public static int getHour(){
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.getHour();
    }

    /**获取分*/
    public static int getMinute(){
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.getMinute();
    }

    /**获取秒*/
    public static int getSecond(){
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.getSecond();
    }

    /**获取秒*/
    public static Long getSecondUTC(){
        return LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
    }

    /**获取秒*/
    public static Long getNowMilliTimeUTC(){
        return LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**获取现在的毫秒*/
    public static long getNowMilliTime(){
        return Instant.now().toEpochMilli();
    }

    /**获取今天的0点*/
    public static String getCurrentDayZeroTime(){
        //获取当前日期
        LocalDate nowDate = LocalDate.now();
        //设置零点
        LocalDateTime beginTime = LocalDateTime.of(nowDate,LocalTime.MIN);
        //将时间进行格式化
        String time = beginTime.format(dateTimeFormatter);
        return time;
    }

    /**获取今天的结束时间*/
    public static String getCurrentDayMaxTime(){
        //获取当前日期
        LocalDate nowDate = LocalDate.now();
        //设置当天的结束时间
        LocalDateTime endTime = LocalDateTime.of(nowDate,LocalTime.MAX);
        //将时间进行格式化
        String time = dateTimeFormatter.format(endTime);
        return time;
    }


    /**
     * 获取本周第一天 以周日为第一天
     */
    public static Date getCurWeekFirstDay(){
        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        Date date = new Date();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -week);
        return calendar.getTime();
    }

    /**
     * 获取当前星期几
     */
    public static int getCurWeek(){
        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return week;
    }

    /**
     * 获取本周开始时间
     */
    public static String getThisWeekStartTime(){
        //获取当前日期
        LocalDate nowDate = LocalDate.now();
        //设置零点
        LocalDateTime beginTime = LocalDateTime.of(nowDate,LocalTime.MIN);
        TemporalAdjuster FIRST_OF_WEEK =
                TemporalAdjusters.ofDateAdjuster(localDate -> localDate.minusDays(localDate.getDayOfWeek().getValue()- DayOfWeek.MONDAY.getValue()));
        String weekStart = dateTimeFormatter.format(beginTime.with(FIRST_OF_WEEK));
        return weekStart;
    }

    /**
     * 获取本周结束时间
     */
    public static String getThisWeekEndTime(){
        //获取当前日期
        LocalDate nowDate = LocalDate.now();
        //设置零点
        LocalDateTime beginTime = LocalDateTime.of(nowDate,LocalTime.MAX);
        TemporalAdjuster LAST_OF_WEEK =
                TemporalAdjusters.ofDateAdjuster(localDate -> localDate.plusDays(DayOfWeek.SUNDAY.getValue() - localDate.getDayOfWeek().getValue()));
        String weekEnd = dateTimeFormatter.format(beginTime.with(LAST_OF_WEEK));
        return weekEnd;
    }

    /**
     * 根据日期获取周
     */
    public static Week dateToWeek(String datetime,DateStyle dateStyle) throws ParseException {
        Date date = stringToDate(datetime,dateStyle.getValue());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return Week.byDay(w);
    }

    /**
     * 根据日期获取周
     */
    public static Week dateToWeek(Date datetime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(datetime);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return Week.byDay(w);
    }

    /**
     * 获取当前月第一天
     */
    public static Date getCurMonthFirstDay(){
        Calendar c = Calendar.getInstance();
        //设置为1号,当前日期既为本月第一天
        c.set(Calendar.DAY_OF_MONTH,c.getActualMinimum(Calendar.DAY_OF_MONTH));
        return c.getTime();
    }

    /**
     * 获取当前月最后一天
     */
    public static Date getCurMonthLastDay(){
        Calendar c = Calendar.getInstance();
        //设置为1号,当前日期既为本月第一天
        c.set(Calendar.DAY_OF_MONTH,c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return  c.getTime();
    }

    /**
     * 获取每周的第一天
     */
    public static String getFirstOfWeek(String dataStr) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(dataStr));
        int d = 0;
        if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
            d = -6;
        } else {
            d = 2 - cal.get(Calendar.DAY_OF_WEEK);
        }
        cal.add(Calendar.DAY_OF_WEEK, d);
        // 所在周开始日期
        String data1 = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        // 所在周结束日期
        //String data2 = new SimpleDateFormat("yyyy/MM/dd").format(cal.getTime());
        return data1;

    }

    /**
     * 获取传入日期所在月的第一天
     */
    public static Date getFirstDayDateOfMonth(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        final int last = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, last);
        return cal.getTime();
    }

    /**
     * 取范围日期的随机日期时间,不含边界
     * 使用方式:randomLocalDateTime(-3,3).format(dateTimeFormatter)
     */
    public static LocalDateTime randomLocalDateTime(int startDay, int endDay) {
        int plusMinus = 1;
        if (startDay < 0 && endDay > 0) {
            plusMinus = Math.random() > 0.5 ? 1 : -1;
            if (plusMinus > 0) {
                startDay = 0;
            } else {
                endDay = Math.abs(startDay);
                startDay = 0;
            }
        } else if (startDay < 0 && endDay < 0) {
            plusMinus = -1;
            //两个数交换
            startDay = startDay + endDay;
            endDay = startDay - endDay;
            startDay = startDay - endDay;
            //取绝对值
            startDay = Math.abs(startDay);
            endDay = Math.abs(endDay);

        }
        //指定时间
        LocalDate day = LocalDate.now().plusDays(plusMinus * RandomUtils.nextInt(startDay, endDay));
        int hour = RandomUtils.nextInt(1, 24);
        int minute = RandomUtils.nextInt(0, 60);
        int second = RandomUtils.nextInt(0, 60);
        LocalTime time = LocalTime.of(hour, minute, second);
        return LocalDateTime.of(day, time);
    }

    /**获取时间差 * */
    public static Map<String,Long> getTimeDiff(String startTime, String endTime) throws ParseException {
        Map<String,Long> map = new HashMap<>(5);
        long milliseconds = getMilliTimeByDate("yyyy-MM-dd HH:mm", startTime) - getMilliTimeByDate("yyyy-MM-dd HH:mm", endTime);
        final long day = TimeUnit.MILLISECONDS.toDays(milliseconds);
        map.put("day",day);
        final long hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliseconds));
        map.put("hours",hours);
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));
        map.put("minutes",minutes);
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds));
        map.put("seconds",seconds);
        final long ms = TimeUnit.MILLISECONDS.toMillis(milliseconds)
                - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(milliseconds));
        map.put("ms",ms);
        return map;
    }

    /**
     * 电影院场景:根据一周的某天获取一周的数据
     * 结果:20230219-20230220-20230221-20230222-20230223-20230224-20230225
     * **/
    public static String thisWeek(String param) throws ParseException {
        StringBuilder stringBuilder = new StringBuilder();
        String s = dateFormatByCustomize(DateStyle.DAY.getValue(), getMilliTimeByDate(DateStyle.YYYY_MM_DD.getValue(), param));
        long time = 24*60*60*1000;
        long milliTimeByDate = getMilliTimeByDate(DateStyle.YYYY_MM_DD.getValue(), param);
        int name =Integer.parseInt(s);
        if (name == 7){
            // 特殊情况 周日 直接获取后面六天的数据
            for (int i = 0;name-i>0;i++){
                long l = milliTimeByDate + time * i;
                String s1 = dateFormatByCustomize(DateStyle.YYYYMMDD.getValue(), l);
                stringBuilder.append(s1).append("-");
            }
        } else {
            // 创建临时变量
            int name1 = name;
            for (int i = 0;name-i>0;i++){
                long l = milliTimeByDate - time * (name-i);
                String s1 = dateFormatByCustomize(DateStyle.YYYYMMDD.getValue(), l);
                stringBuilder.append(s1).append("-");
            }
            int j = 0;
            for (int i = 7;i-name1>0;name1++){
                long l = milliTimeByDate + time * (j++);
                String s1 = dateFormatByCustomize(DateStyle.YYYYMMDD.getValue(), l);
                stringBuilder.append(s1).append("-");
            }
        }
        stringBuilder.delete(stringBuilder.length()-1,stringBuilder.length());
        return stringBuilder.toString();
    }

    /**
     * 电影院场景:根据一周的某天获取一周的数据
     * 结果:[2023-02-19, 2023-02-20, 2023-02-21, 2023-02-22, 2023-02-23, 2023-02-24, 2023-02-25]
     * **/
    public static List<String> thisWeekList(String param) throws ParseException {
        List<String> list = new ArrayList<>(16);
        String s = dateFormatByCustomize(DateStyle.DAY.getValue(), getMilliTimeByDate(DateStyle.YYYY_MM_DD.getValue(), param));
        long time = 24*60*60*1000;
        long milliTimeByDate = getMilliTimeByDate(DateStyle.YYYY_MM_DD.getValue(), param);
        int name =Integer.parseInt(s);
        if (name == 7){
            // 特殊情况 周日 直接获取后面六天的数据
            for (int i = 0;name-i>0;i++){
                long l = milliTimeByDate + time * i;
                String s1 = dateFormatByCustomize(DateStyle.YYYY_MM_DD.getValue(), l);
                list.add(s1);
            }
        } else {
            // 创建临时变量
            int name1 = name;
            for (int i = 0;name-i>0;i++){
                long l = milliTimeByDate - time * (name-i);
                String s1 = dateFormatByCustomize(DateStyle.YYYY_MM_DD.getValue(), l);
                list.add(s1);
            }
            int j = 0;
            for (int i = 7;i-name1>0;name1++){
                long l = milliTimeByDate + time * (j++);
                String s1 = dateFormatByCustomize(DateStyle.YYYY_MM_DD.getValue(), l);
                list.add(s1);
            }
        }
        return list;
    }

    /**
     *  获取所有的时区
     */
    public static Set<String> availableZoneIds(){
        return ZoneId.getAvailableZoneIds();
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }
}
