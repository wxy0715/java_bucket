package com.cjree.core.basic.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public abstract class AbsTLogWebHandlerMethodInterceptor implements HandlerInterceptor {

    public abstract boolean preHandleByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;

    public abstract void postHandleByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception;

    public abstract void afterCompletionByHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            return preHandleByHandlerMethod(request, response, handler);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (handler instanceof HandlerMethod) {
            postHandleByHandlerMethod(request, response, handler, modelAndView);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            afterCompletionByHandlerMethod(request, response, handler, ex);
        }
    }

}
