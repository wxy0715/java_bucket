package com.wxy.study.wechat.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.wxy.study.wechat.common.BaseResponseVO;
import com.wxy.study.wechat.conf.WechatTemplateProperties;
import com.wxy.study.wechat.service.WechatTemplateService;
import com.wxy.study.wechat.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/v1")
public class WechatTemplateController {

    @Autowired
    private WechatTemplateProperties properties;

    @Autowired
    private WechatTemplateService wechatTemplateService;

    @RequestMapping(value = "/template", method = RequestMethod.GET)
    public BaseResponseVO getTemplate(){

        WechatTemplateProperties.WechatTemplate wechatTemplate = wechatTemplateService.getWechatTemplate();

        Map<String, Object> result = Maps.newHashMap();
        result.put("templateId",wechatTemplate.getTemplateId());
        result.put("template", FileUtils.readFile2JsonArray(wechatTemplate.getTemplateFilePath()));

        return BaseResponseVO.success(result);
    }

    @RequestMapping(value = "/template/result", method = RequestMethod.GET)
    public BaseResponseVO templateStatistics(
            @RequestParam(value = "templateId", required = false)String templateId){

        JSONObject statistics = wechatTemplateService.templateStatistics(templateId);

        return BaseResponseVO.success(statistics);
    }

    @RequestMapping(value = "/template/report", method = RequestMethod.POST)
    public BaseResponseVO dataReported(
            @RequestBody String reportData){

        wechatTemplateService.templateReported(JSON.parseObject(reportData));

        return BaseResponseVO.success();
    }

}
