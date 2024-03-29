package com.wxy.utils.file;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.StringTokenizer;

/**
 * @author wxy
 * @description 常用工具类
 * @data 2020/12/20
 */
public class SystemUtils {
    /** wxy- 获取request**/
    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**判断是否是ajax**/
    public static boolean isAjaxRequest(HttpServletRequest request){

        String accept = request.getHeader("accept");
        String xRequestedWith = request.getHeader("X-Requested-With");

        // 如果是异步请求或是手机端，则直接返回信息
        return ((accept != null && accept.contains("application/json")
                || (xRequestedWith != null && xRequestedWith.contains("XMLHttpRequest"))
        ));
    }


    /** 获取主机ip**/
    public static String getServerIp(final HttpServletRequest request){
        return request.getHeader("Host");
    }

    /** 获取来源ip**/
    public static String getRemoteIP(HttpServletRequest request) {
        try{
            String remoteAddr = request.getHeader("X-Forwarded-For");
            // 如果通过多级反向代理，X-Forwarded-For的值不止一个，而是一串用逗号分隔的IP值，此时取X-Forwarded-For中第一个非unknown的有效IP字符串
            if (isEffective(remoteAddr) && (remoteAddr.indexOf(",") > -1)) {
                String[] array = remoteAddr.split(",");
                for (String element : array) {
                    if (isEffective(element)) {
                        remoteAddr = element;
                        break;
                    }
                }
            }
            if (!isEffective(remoteAddr)) {
                remoteAddr = request.getHeader("X-Real-IP");
            }
            if (!isEffective(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
            return remoteAddr;
        }catch(Exception e){
            return "";
        }
    }

    /**
     * 远程地址是否有效.
     * @param remoteAddr 远程地址
     * @return true代表远程地址有效，false代表远程地址无效
     */
    private static boolean isEffective(final String remoteAddr) {
        boolean isEffective = false;
        if ((null != remoteAddr) && (!"".equals(remoteAddr.trim()))
                && (!"unknown".equalsIgnoreCase(remoteAddr.trim()))) {
            isEffective = true;
        }
        return isEffective;
    }

    /** wxy- 判断系统类型**/
    public static boolean osType(){
        HttpServletRequest request = getHttpServletRequest();
        String agent=request.getHeader("User-Agent");
        StringTokenizer st = new StringTokenizer(agent,";");
        st.nextToken();
        String userbrowser = st.nextToken().toLowerCase();
        if (userbrowser.contains("win")) {
            return true;
        }
        return false;
    }

}
