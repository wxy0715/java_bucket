package com.wxy.seata1.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 年龄新枚举
 *
 * @author wangxingyu
 * @date 2023/01/06
 */
public enum SexEnum implements IEnum<String> {
    MAN( "男"),
    WOMEN( "女");

    @JsonValue
    private final String value;

    SexEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }


}
