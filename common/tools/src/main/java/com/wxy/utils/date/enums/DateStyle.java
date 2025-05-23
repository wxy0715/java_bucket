package com.wxy.utils.date.enums;

public enum DateStyle {
    DAY("u"),WEEK_YEAR("w"),YEAR("yyyy"),
    YYYY_MM("yyyy-MM"), YYYY_MM_DD("yyyy-MM-dd"), YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm"),
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
    YYYY_MM_EN("yyyy/MM"), YYYY_MM_DD_EN("yyyy/MM/dd"),
    YYYY_MM_DD_HH_MM_EN("yyyy/MM/dd HH:mm"), YYYY_MM_DD_HH_MM_SS_EN("yyyy/MM/dd HH:mm:ss"),
    YYYYMMDD("yyyyMMdd"), YYYY_MM_DD_CN("yyyy年MM月dd日"),
    YYYY_MM_DD_HH_MM_CN("yyyy年MM月dd日 HH:mm"), YYYY_MM_DD_HH_MM_SS_CN("yyyy年MM月dd日 HH:mm:ss"),
    HH_MM("HH:mm"), HH_MM_SS("HH:mm:ss"),
    MM_DD("MM-dd"), MM_DD_HH_MM("MM-dd HH:mm"), MM_DD_HH_MM_SS("MM-dd HH:mm:ss"),
    MM_DD_EN("MM/dd"), MM_DD_HH_MM_EN("MM/dd HH:mm"), MM_DD_HH_MM_SS_EN("MM/dd HH:mm:ss"),
    MM_DD_CN("MM月dd日"), MM_DD_HH_MM_CN("MM月dd日 HH:mm"),
    yyyyMMddHHmmssSSS("yyyyMMddHHmmssSSSS"), MM_DD_HH_MM_SS_CN("MM月dd日 HH:mm:ss");

    /**
     时间格式
     */
    private String value;

    DateStyle(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}