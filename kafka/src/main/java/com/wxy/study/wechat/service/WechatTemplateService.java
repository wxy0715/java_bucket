package com.wxy.study.wechat.service;


import com.alibaba.fastjson.JSONObject;
import com.wxy.study.wechat.conf.WechatTemplateProperties;


public interface WechatTemplateService {

    // 获取微信调查问卷模板 - 获取目前active为true的模板就可以了
    WechatTemplateProperties.WechatTemplate getWechatTemplate();

    // 上报调查问卷填写结果
    void templateReported(JSONObject reportInfo);

    // 获取调查问卷的统计结果
    JSONObject templateStatistics(String templateId);

}
