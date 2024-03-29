package com.wxy.utils.date.enums;


public enum Week {
    Mon("星期一", "Monday",2),
    Tue("星期二", "Tuesday",3),
    Wed("星期三", "Wednesday",4),
    Thu("星期四", "Thursday",5),
    Fri("星期五", "Friday",6),
    Sat("星期六", "Saturday",7),
    Sun("星期日", "Sunday",1);
    final String zh;
    final String en;
    final Integer day;

    Week(String zh, String en,Integer day) {
        this.zh = zh;
        this.en = en;
        this.day = day;
    }

    public String value() {
        return this.zh;
    }

    public String zh() {
        return this.zh;
    }

    public String en() {
        return this.en;
    }
    public Integer day() {
        return this.day;
    }


    public static Week byDay(Integer data){
        for (Week week : Week.values()) {
            if (week.day().equals(data)) {
                return week;
            }
        }
        return null;
    }
}
