package com.cjree.core.basic.log.rpc;

import cn.hutool.core.util.IdUtil;
import com.cjree.core.basic.log.LocalhostUtil;
import com.cjree.core.common.log.TLogConstants;
import com.cjree.core.common.log.TLogContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * TLog的RPC处理逻辑的封装类
 *
 * @author Bryan.Zhang
 * @since 1.2.0
 */
public class TLogRPCHandler {

    protected static final Logger log = LoggerFactory.getLogger(TLogRPCHandler.class);

    public void processProviderSide(TLogLabelBean labelBean) {
        if (StringUtils.isBlank(labelBean.getPreIvkApp())) {
            labelBean.setPreIvkApp(TLogConstants.UNKNOWN);
        }
        TLogContext.putPreIvkApp(labelBean.getPreIvkApp());

        if (StringUtils.isBlank(labelBean.getPreIvkHost())) {
            labelBean.setPreIvkHost(TLogConstants.UNKNOWN);
        }
        TLogContext.putPreIvkHost(labelBean.getPreIvkHost());

        if (StringUtils.isBlank(labelBean.getPreIp())) {
            labelBean.setPreIp(TLogConstants.UNKNOWN);
        }
        TLogContext.putPreIp(labelBean.getPreIp());

        //如果没有获取到，则重新生成一个traceId
        if (StringUtils.isBlank(labelBean.getTraceId())) {
            labelBean.setTraceId(IdUtil.getSnowflakeNextIdStr());
            log.debug("[TLOG]可能上一个节点[{}]没有正确传递traceId,重新生成traceId[{}]", labelBean.getPreIvkApp(), labelBean.getTraceId());
        }

        try {
            TLogContext.putThreadId(String.valueOf(Thread.currentThread().getId()));
        } catch (Exception e) {
            log.error("[TLOG]获取当前线程ID出错", e);
        }

        //往TLog上下文里放当前获取到的spanId，如果spanId为空，会放入初始值
        TLogContext.putSpanId(labelBean.getSpanId());

        //往TLog上下文里放一个当前的threadId
        TLogContext.putTraceId(labelBean.getTraceId());

        //往TLog上下文里放一个当前的traceId
        TLogContext.putTraceId(labelBean.getTraceId());

        //往TLog上下文里放当前的IP
        TLogContext.putCurrIp(LocalhostUtil.getHostIp());

        //目前无论是不是MDC，都往MDC里放参数，这样就避免了log4j2的特殊设置
        MDC.put(TLogConstants.TLOG_THREAD_KEY, TLogContext.getThreadId());
        MDC.put(TLogConstants.TLOG_TRACE_KEY, TLogContext.getTraceId());
        MDC.put(TLogConstants.TLOG_SPANID_KEY, TLogContext.getSpanId());
        MDC.put(TLogConstants.CURR_IP_KEY, TLogContext.getCurrIp());
        MDC.put(TLogConstants.PRE_IP_KEY, TLogContext.getPreIp());
        MDC.put(TLogConstants.PRE_IVK_APP_HOST, TLogContext.getPreIvkHost());
        MDC.put(TLogConstants.PRE_IVK_APP_KEY, TLogContext.getPreIvkApp());
    }

    public void cleanThreadLocal() {
        //移除ThreadLocal里的数据
        TLogContext.removePreIvkApp();
        TLogContext.removePreIvkHost();
        TLogContext.removePreIp();
        TLogContext.removeCurrIp();
        TLogContext.removeThreadId();
        TLogContext.removeTraceId();
        TLogContext.removeSpanId();

        //移除MDC里的信息
        MDC.remove(TLogConstants.TLOG_THREAD_KEY);
        MDC.remove(TLogConstants.TLOG_TRACE_KEY);
        MDC.remove(TLogConstants.TLOG_SPANID_KEY);
        MDC.remove(TLogConstants.CURR_IP_KEY);
        MDC.remove(TLogConstants.PRE_IP_KEY);
        MDC.remove(TLogConstants.PRE_IVK_APP_HOST);
        MDC.remove(TLogConstants.PRE_IVK_APP_KEY);
    }
}
