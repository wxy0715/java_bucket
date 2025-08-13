package com.cjree.core.basic.log.mq;

import com.cjree.core.basic.log.LocalhostUtil;
import com.cjree.core.basic.log.rpc.TLogLabelBean;
import com.cjree.core.common.config.SpringContainer;
import com.cjree.core.common.log.SpanIdGenerator;
import com.cjree.core.common.log.TLogContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * 针对于Mq的包装Bean
 */
public class TLogMqWrapBean<T> extends TLogLabelBean implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(TLogMqWrapBean.class);
    private static final long serialVersionUID = -9125086965312434222L;

    private T t;

    public TLogMqWrapBean() {
    }

    public TLogMqWrapBean(T t) {
        this.t = t;
        String traceId = TLogContext.getTraceId();

        if (StringUtils.isNotBlank(traceId)) {
            String appName = SpringContainer.getProperty("spring.application.name");

            this.setTraceId(traceId);
            this.setPreIvkApp(appName);
            this.setPreIvkHost(LocalhostUtil.getHostName());
            this.setPreIp(LocalhostUtil.getHostIp());
            this.setSpanId(SpanIdGenerator.generateNextSpanId());
        } else {
            log.warn("[TLOG]本地kafka客户端没有正确传递traceId,本次发送不传递traceId");
        }
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
