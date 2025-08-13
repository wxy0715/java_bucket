package com.cjree.core.basic.base;

/**
 * 接口返回响应结果参数枚举
 */
public enum ResponseParameter {

    /**
     * 返回参数：数据
     */
    data,
    /**
     * 返回参数：当前页
     */
    pageIndex,
    /**
     * 返回参数：页大小
     */
    pageSize,
    /**
     * 返回参数：总页数
     */
    pageCount,
    /**
     * 返回参数：总条数
     */
    total,
    /**
     * 返回参数：状态码
     */
    code,
    /**
     * 返回参数：描述
     */
    description,
    /**
     * 返回参数：当前时间（毫秒数）
     */
    currentTime,
    /**
     * 单次访问唯一标识码
     */
    rid
}
