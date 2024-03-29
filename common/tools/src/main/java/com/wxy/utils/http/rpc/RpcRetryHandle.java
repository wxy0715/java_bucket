package com.wxy.utils.http.rpc;


import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

/**
 * 请求连接池失败重试策略
 */
public class RpcRetryHandle implements HttpRequestRetryHandler {
    private static final Logger logger = LoggerFactory.getLogger(RpcRetryHandle.class);
    //请求失败时,进行请求重试
    @Override
    public boolean retryRequest(IOException e, int i, HttpContext httpContext) {
        if (i > 3) {
            //重试超过3次,放弃请求
            logger.error("retry has more than 3 time, give up request");
            return false;
        }
        if (e instanceof NoHttpResponseException) {
            //服务器没有响应,可能是服务器断开了连接,应该重试
            logger.error("receive no response from server, retry");
            return true;
        }
        if (e instanceof SSLHandshakeException) {
            // SSL握手异常
            logger.error("SSL hand shake exception");
            return false;
        }
        if (e instanceof InterruptedIOException) {
            //超时
            logger.error("InterruptedIOException");
            return false;
        }
        if (e instanceof UnknownHostException) {
            // 服务器不可达
            logger.error("server host unknown");
            return false;
        }
        if (e instanceof ConnectTimeoutException) {
            // 连接超时
            logger.error("Connection Time out");
            return false;
        }
        if (e instanceof SSLException) {
            logger.error("SSLException");
            return false;
        }
        HttpClientContext context = HttpClientContext.adapt(httpContext);
        HttpRequest request = context.getRequest();
        if (!(request instanceof HttpEntityEnclosingRequest)) {
            //如果请求不是关闭连接的请求
            return true;
        }
        return false;
    }
}