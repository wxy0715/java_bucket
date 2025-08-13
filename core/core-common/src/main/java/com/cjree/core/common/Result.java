package com.cjree.core.common;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class Result<T> {

    private String code;
    private String description;
    private Long currentTime;
    // 链路id
    private String traceId;
    // 链路的深度
    private String spanId;
    private int pageIndex;
    private int pageSize;
    private int pageCount;
    private int total;
    private List<T> data = new ArrayList<>();

    // 成功返回对象
    public Result(){
    }

    // 成功返回对象
    public Result(Object data){
        this.code = com.cjree.core.common.ResponseCode.SUCCESS.value();
        this.description = com.cjree.core.common.ResponseCode.SUCCESS.message();
        if (data instanceof List) {
            this.data = (List)data;
        } else {
            this.data = Collections.singletonList((T) data);
        }
    }

    public Result(com.cjree.core.common.ResponseCode responseCode, Object data) {
        this.code = responseCode.value();
        this.description = responseCode.message();
        if (data instanceof List) {
            this.data = (List)data;
        } else {
            this.data = Collections.singletonList((T) data);
        }
    }

}
