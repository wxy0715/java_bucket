package com.wxy.utils.date.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

/** 发薪日是15号,如果发薪日是周末,则修改为周五*/
public class PayDayAdjuster implements TemporalAdjuster {
    @Override
    public Temporal adjustInto(Temporal temporal) {
        LocalDate payday = LocalDate.from(temporal);
        int day;
        if (payday.getDayOfMonth() != 15) {
            day = 15;
        } else {
            day = payday.getDayOfMonth();
        }
        // 修改时间为15号
        LocalDate realDay = payday.withDayOfMonth(day);
        if (realDay.getDayOfWeek() == DayOfWeek.SUNDAY || realDay.getDayOfWeek() == DayOfWeek.SATURDAY) {
            realDay = realDay.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));
        }
        return realDay;
    }

    public static void main(String[] args) {
        LocalDate localDate = LocalDate.of(2021, 11, 15);
        LocalDate from = LocalDate.from(new PayDayAdjuster().adjustInto(localDate));
        System.out.println(localDate.getDayOfMonth());
        System.out.println(localDate.getDayOfWeek());
        System.out.println(from.getDayOfMonth());
        System.out.println(from.getDayOfWeek());
    }
}
