package com.wxy.retry.service;


public interface IBusinessService {


    /**
     * 服务重试
     *
     * @param msg 消息
     * @return 结果
     * @author zhengqingya
     * @date 2022/4/6 10:20
     */
    String retry(String msg);

}
