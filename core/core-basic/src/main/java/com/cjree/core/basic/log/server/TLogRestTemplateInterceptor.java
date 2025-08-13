package com.cjree.core.basic.log.server;

import com.cjree.core.basic.log.LocalhostUtil;
import com.cjree.core.common.config.SpringContainer;
import com.cjree.core.common.log.SpanIdGenerator;
import com.cjree.core.common.log.TLogConstants;
import com.cjree.core.common.log.TLogContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * RestTemplate的拦截器
 * @author Bryan.Zhang
 * @since 1.3.6
 */
public class TLogRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String traceId = TLogContext.getTraceId();
        if(StringUtils.isNotBlank(traceId)) {
            String appName = SpringContainer.getProperty("spring.application.name");

            request.getHeaders().add(TLogConstants.TLOG_TRACE_KEY, traceId);
            request.getHeaders().add(TLogConstants.TLOG_SPANID_KEY, SpanIdGenerator.generateNextSpanId());
            request.getHeaders().add(TLogConstants.PRE_IVK_APP_KEY, appName);
            request.getHeaders().add(TLogConstants.PRE_IVK_APP_HOST, LocalhostUtil.getHostName());
            request.getHeaders().add(TLogConstants.PRE_IP_KEY, LocalhostUtil.getHostIp());
        } else {
            log.debug("[TLOG]本地threadLocal变量没有正确传递traceId,本次调用不传递traceId");
        }
        return execution.execute(request, body);
    }
}
