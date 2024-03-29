package com.wxy.seata.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 其他信息
 */
@Data
@AllArgsConstructor
public class OtherInfo {
    /**
     * 性别
     */
    private String sex;
    /**
     * 居住城市
     */
    private String city;


    public OtherInfo group(OtherInfo otherInfo) {

        return this;
    }
}