package com.wxy.utils.http;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.wxy.utils.http.rpc.IdleConnectionMonitorThread;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 依赖的jar包有：commons-lang-2.6.jar、httpclient-4.3.2.jar、httpcore-4.3.1.jar、commons-io-2.4.jar
 * @author wangxingyu
 */
@Component
public class RpcUtils implements DisposableBean {
	private static final Logger log = LoggerFactory.getLogger(RpcUtils.class);
	/**获取连接超时时间 */
	public static final int CONNECT_TIMEOUT = 10000;
	/**请求超时时间 */
	public static final int CONNECT_REQUEST_TIMEOUT = 10000;
	/** 读超时时间*/
	public static final int SOCKET_TIMEOUT = 180000;
	public static CloseableHttpClient client;
	/** 设置请求参数*/
	public static final RequestConfig REQUEST_CONFIG;
	/**池化,解决http请求的多线程问题 */
	public static final PoolingHttpClientConnectionManager CONNECTION_MANAGER;
	public static BasicCookieStore cookieStore = new BasicCookieStore();
	private static IdleConnectionMonitorThread idleConnectionMonitorThread;
	static {
		// 初始化连接池
		CONNECTION_MANAGER = new PoolingHttpClientConnectionManager();
		// 默认连接配置
		ConnectionConfig connectConfig = ConnectionConfig.custom().setCharset(StandardCharsets.UTF_8).build();
		SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(5000).build();
		CONNECTION_MANAGER.setDefaultConnectionConfig(connectConfig);
		CONNECTION_MANAGER.setDefaultSocketConfig(socketConfig);
		// http请求线程池，最大连接数
		CONNECTION_MANAGER.setMaxTotal(200);
		// 默认设置route最大连接数,并行接收的请求数量
		CONNECTION_MANAGER.setDefaultMaxPerRoute(100);
		//设置请求参数
		REQUEST_CONFIG = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT)
				.setConnectionRequestTimeout(CONNECT_REQUEST_TIMEOUT)
				.setSocketTimeout(SOCKET_TIMEOUT)
				.build();
		// 创建builder
		HttpClientBuilder builder = HttpClients.custom();
		//管理器是共享的，它的生命周期将由调用者管理，并且不会关闭,否则可能出现Connection pool shut down异常
		builder.setConnectionManager(CONNECTION_MANAGER).setConnectionManagerShared(true);
		// 长连接策略
		builder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());
		// 默认请求配置
		builder.setDefaultRequestConfig(REQUEST_CONFIG);
		// tlog-trace拦截器
		//builder.addInterceptorFirst(new TLogHttpClientInterceptor());
		// cookie
		builder.setDefaultCookieStore(cookieStore);
		// 重试
		builder.setRetryHandler(new DefaultHttpRequestRetryHandler(3,true));
		//builder.setRetryHandler(new RpcRetryHandle());
		ConnectionKeepAliveStrategy keepAliveStrategy = (response, context) -> {
			HeaderElementIterator it = new BasicHeaderElementIterator
					(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
			while (it.hasNext()) {
				HeaderElement he = it.nextElement();
				String param = he.getName();
				String value = he.getValue();
				if (value != null && param.equalsIgnoreCase("timeout")) {
					return Long.parseLong(value) * 1000;
				}
			}
			return 60 * 1000;//如果没有约定，则默认定义时长为60s
		};
		// 设置keep-alive
		builder.setKeepAliveStrategy(keepAliveStrategy);
		// 创建SSL连接
		SSLContext sslContext = null;
		try {
			sslContext = new SSLContextBuilder().loadTrustMaterial(null, (TrustStrategy) (chain, authType) -> true).build();
		} catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
			throw new RuntimeException(e);
		}
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, (arg0, arg1) -> true);
		builder.setSSLSocketFactory(sslsf);
		client = builder.build();
		// 心跳线程
		idleConnectionMonitorThread = new IdleConnectionMonitorThread(CONNECTION_MANAGER);
		idleConnectionMonitorThread.start();
	}

	/**发送一个 GET 请求 */
	public static String get(String url) throws Exception {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		log.info("HTTPCLIENT->请求地址为:{}",url);
		HttpGet get = new HttpGet(url);
		String result;
		try {
			HttpResponse res;
			res = client.execute(get);
			result = IOUtils.toString(res.getEntity().getContent(), StandardCharsets.UTF_8);
		} finally {
			get.releaseConnection();
			stopWatch.stop();
			log.info("HTTPCLIENT耗时:{}ms->请求接口{}",stopWatch.getTime(),url);
		}
		return result;
	}

	/** post请求 */
	public static JSONObject doPostHttps(String url, Map<String, String> headers, String param) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		log.info("HTTPCLIENT->请求地址为:{},参数为:{}",url,param);
		HttpResponse res;
		HttpPost post = new HttpPost(url);
		JSONObject response = null;
		try {
			for (Entry<String, String> elem : headers.entrySet()) {
				post.addHeader(elem.getKey(), elem.getValue());
			}
			post.setEntity(new StringEntity(param, StandardCharsets.UTF_8));
			res = client.execute(post);
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 获取cookie
				List<Cookie> cookies = cookieStore.getCookies();
				if (ObjectUtil.isNotEmpty(cookies)) {
					log.info("HTTPCLIENT-COOKIE->请求接口{} cookie为:{}",url,cookies);
				}
				// 获取返回结果
				HttpEntity entity = res.getEntity();
				String result = EntityUtils.toString(entity);
				response = JSONObject.parseObject(result);
				if (ObjectUtil.isEmpty(response)) {
					throw new RuntimeException("请求外部接口失败");
				}
				response.put("cookie",cookieStore.getCookies());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			stopWatch.stop();
			log.info("HTTPCLIENT耗时:{}ms->请求接口{}",stopWatch.getTime(),url);
		}
		return response;
	}

	@Override
	public void destroy() throws Exception {
		idleConnectionMonitorThread.shutdown();
	}
}
