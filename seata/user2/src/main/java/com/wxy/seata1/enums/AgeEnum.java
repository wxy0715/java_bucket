package com.wxy.seata1.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AgeEnum {
    ONE(1, "一岁"),
    TWO(2, "二岁"),
    THREE(3, "三岁");

    @EnumValue
    @JsonValue
    private final int value;

    private final String desc;

    AgeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

}
