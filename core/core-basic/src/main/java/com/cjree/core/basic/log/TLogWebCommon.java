package com.cjree.core.basic.log;

import cn.hutool.core.util.StrUtil;
import com.cjree.core.basic.log.rpc.TLogLabelBean;
import com.cjree.core.basic.log.rpc.TLogRPCHandler;
import com.cjree.core.common.log.TLogConstants;
import com.cjree.core.common.log.TLogContext;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TLog web这块的逻辑封装类
 */
public class TLogWebCommon extends TLogRPCHandler {

    private final static Logger log = LoggerFactory.getLogger(TLogWebCommon.class);

    private static volatile TLogWebCommon tLogWebCommon;

    public static TLogWebCommon loadInstance() {
        if (tLogWebCommon == null) {
            synchronized (TLogWebCommon.class) {
                if (tLogWebCommon == null) {
                    tLogWebCommon = new TLogWebCommon();
                }
            }
        }
        return tLogWebCommon;
    }

    public void preHandle(HttpServletRequest request) {
        String traceId = StrUtil.nullToDefault(TLogContext.getTraceId(), request.getHeader(TLogConstants.TLOG_TRACE_KEY));
        String spanId = StrUtil.nullToDefault(TLogContext.getSpanId(), request.getHeader(TLogConstants.TLOG_SPANID_KEY));
        String preIvkApp = StrUtil.nullToDefault(TLogContext.getPreIvkApp(), request.getHeader(TLogConstants.PRE_IVK_APP_KEY));
        String preIvkHost = StrUtil.nullToDefault(TLogContext.getPreIvkHost(), request.getHeader(TLogConstants.PRE_IVK_APP_HOST));
        String preIp = StrUtil.nullToDefault(TLogContext.getPreIp(), request.getHeader(TLogConstants.PRE_IP_KEY));

        TLogLabelBean labelBean = new TLogLabelBean(preIvkApp, preIvkHost, preIp, traceId, spanId);

        processProviderSide(labelBean);
    }

    public void afterCompletion() {
        cleanThreadLocal();
    }
}
