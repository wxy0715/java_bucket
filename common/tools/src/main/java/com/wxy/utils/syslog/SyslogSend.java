package com.wxy.utils.syslog;


import com.alibaba.fastjson.JSONObject;
import org.graylog2.syslog4j.Syslog;
import org.graylog2.syslog4j.SyslogConstants;
import org.graylog2.syslog4j.SyslogIF;
import org.graylog2.syslog4j.impl.net.udp.UDPNetSyslogConfig;

import java.net.URLDecoder;
import java.util.Date;

public class SyslogSend {
	private static final String ENC = "utf-8";
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 514;

	public static void main(String[] args) {
		new SyslogSend().send(System.currentTimeMillis() + "", "这是一条测试日志");
	}

	public void send(String traceId, String msg) {
		SyslogIF syslog = Syslog.getInstance(SyslogConstants.UDP);
		UDPNetSyslogConfig udpNetSyslog = (UDPNetSyslogConfig)syslog.getConfig();
		udpNetSyslog.setHost(HOST);
		udpNetSyslog.setPort(PORT);
		udpNetSyslog.setSendLocalName(true);//携带本机主机名
		udpNetSyslog.setCacheHostAddress(false);
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("traceId", traceId);
			jsonObject.put("time", new Date().toString().substring(4, 20));
			jsonObject.put("message", msg);
			jsonObject.put("level", 0);
			syslog.log(0, URLDecoder.decode(jsonObject.toString(), ENC));

			jsonObject.put("level", 1);
			syslog.log(1, URLDecoder.decode(jsonObject.toString(), ENC));

			jsonObject.put("level", null);
			jsonObject.put("type", "info");
			syslog.info(URLDecoder.decode(jsonObject.toString(), ENC));

			jsonObject.put("type", "error");
			syslog.error(URLDecoder.decode(jsonObject.toString(), ENC));

			jsonObject.put("type", "warn");
			syslog.warn(URLDecoder.decode(jsonObject.toString(), ENC));
		} catch (Exception e) {
			System.out.println("send exception " + e.getMessage());
		}
	}
        /* syslog.log——发送信息到服务器，数字表示日志级别 范围为0~7的数字编码，表示了事件的严重程度。0最高，7最低
        * syslog为每个事件赋予几个不同的优先级：
        0 LOG_EMERG：紧急情况，需要立即通知技术人员。
        1 LOG_ALERT：应该被立即改正的问题，如系统数据库被破坏，ISP连接丢失。
        2 LOG_CRIT：重要情况，如硬盘错误，备用连接丢失。
        3 LOG_ERR：错误，不是非常紧急，在一定时间内修复即可。
        4 LOG_WARNING：警告信息，不是错误，比如系统磁盘使用了85%等。
        5 LOG_NOTICE：不是错误情况，也不需要立即处理。
        6 LOG_INFO：情报信息，正常的系统消息，比如骚扰报告，带宽数据等，不需要处理。
        7 LOG_DEBUG：包含详细的开发情报的信息，通常只在调试一个程序时使用。
        */

}