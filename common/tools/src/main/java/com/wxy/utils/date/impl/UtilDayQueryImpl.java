package com.wxy.utils.date.impl;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;

/** 距离下一个劳动节还有多少天*/
public class UtilDayQueryImpl implements TemporalQuery<Long> {
    @Override
    public Long queryFrom(TemporalAccessor temporal) {
        LocalDate now = LocalDate.from(temporal);
        LocalDate target = LocalDate.of(now.getYear(), Month.MAY, 1);
        if (now.isAfter(target)) {
            target = target.plusYears(1);
        }
        return ChronoUnit.DAYS.between(now, target);
    }

    public static void main(String[] args) {
        LocalDate now = LocalDate.now();
        Long query = now.query(new UtilDayQueryImpl());
        System.out.println(query);
    }
}
