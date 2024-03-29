package com.wxy.arthas.interceptor;

import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.reflection.ForestMethod;
import com.wxy.arthas.entity.BusinessProjectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

public class ForestInterceptor implements Interceptor<BusinessProjectResult> {

    private final static Logger log = LoggerFactory.getLogger(ForestInterceptor.class);

    /**
     * 该方法在被调用时，并在beforeExecute前被调用
     * @param  req Forest请求对象
     * @param method method
     */
    @Override
    public void onInvokeMethod(ForestRequest req, ForestMethod method, Object[] args) {

    }

    /**
     * 该方法在请求发送之前被调用, 若返回false则不会继续发送请求
     * @param req Forest请求对象
     */
    @Override
    public boolean beforeExecute(ForestRequest req) {
        // 添加请求头
        // req.addHeader("accessToken", "11111111");
        // 添加请求参数
        // req.addQuery("username", "foo");
        return true;
    }

    /**
     * 在请求体数据序列化后，发送请求数据前调用该方法
     * 默认为什么都不做
     * 注: multlipart/data类型的文件上传格式的 Body 数据不会调用该回调函数
     * @param request Forest请求对象
     * @param encoder Forest转换器
     * @param encodedData 序列化后的请求体数据
     */
    @Override
    public byte[] onBodyEncode(ForestRequest request, ForestEncoder encoder, byte[] encodedData) {
        // request: Forest请求对象
        // encoder: 此次转换请求数据的序列化器
        // encodedData: 序列化后的请求体字节数组
        // 返回的字节数组将替换原有的序列化结果
        // 默认不做任何处理，直接返回参数 encodedData
        return encodedData;
    }

    /**
     * 该方法在请求成功响应时被调用
     */
    @Override
    public void onSuccess(BusinessProjectResult data, ForestRequest req, ForestResponse res) {
        log.info("获取请求的成功响应内容:{}",res.getContent());
        if (!Objects.equals(data.getIsSuccess(), Boolean.TRUE)) {
            String msg = !ObjectUtils.isEmpty(data.getErrorMessage()) ? data.getErrorMessage() : "";
            throw new RuntimeException("请求核算接口失败[" + msg + "]");
        }
    }


    /**
     * 该方法在请求发送失败时被调用
     */
    @Override
    public void onError(ForestRuntimeException ex, ForestRequest req, ForestResponse res) {
        log.error("获取请求的失败内容:{},失败状态码:{}",res.getContent(),res.getStatusCode());
    }

    /**
     * 该方法在请求发送之后被调用
     */
    @Override
    public void afterExecute(ForestRequest req, ForestResponse res) {
        //log.info("获取请求的最终内容:{},状态码:{}",res.getContent(),res.getStatusCode());
    }
}

