package com.cjree.core.basic.log.config;

import com.cjree.core.basic.log.AbsTLogWebHandlerMethodInterceptor;
import com.cjree.core.basic.log.TLogWebCommon;
import com.cjree.core.common.log.TLogConstants;
import com.cjree.core.common.log.TLogContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;


/**
 * web controller的拦截器
 */
public class TLogWebInterceptor extends AbsTLogWebHandlerMethodInterceptor {

    @Override
    public boolean preHandleByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        TLogWebCommon.loadInstance().preHandle(request);
        //把traceId放入response的header，为了方便有些人有这样的需求，从前端拿整条链路的traceId
        response.addHeader(TLogConstants.TLOG_TRACE_KEY, TLogContext.getTraceId());
        return true;
    }

    @Override
    public void postHandleByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    /**
     *  清理请求上下文资源,防止内存泄露
     */
    @Override
    public void afterCompletionByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TLogWebCommon.loadInstance().afterCompletion();
    }
}
