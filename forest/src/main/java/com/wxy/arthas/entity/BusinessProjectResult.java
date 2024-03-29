package com.wxy.arthas.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BusinessProjectResult {
    private static final Logger log = LoggerFactory.getLogger(Class.class);
    private Boolean isSuccess;

    private String data;

    private String errorCode;

    private String errorMessage;

    public <T> List<T> convertList(Class<T> clazz){
        List<T> list = null;
        try {
            list = JSONArray.parseArray(this.getData(), clazz);
        } catch (Exception exception) {
            log.error("转为目标类{}异常",clazz);
            throw new RuntimeException("转换异常");
        }
        return list;
    }

}
