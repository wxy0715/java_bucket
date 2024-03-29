package com.wxy.utils.sysinfo;

import org.graylog2.syslog4j.Syslog;
import org.graylog2.syslog4j.SyslogConstants;
import org.graylog2.syslog4j.SyslogIF;

import java.io.*;
import java.net.URLDecoder;

public class SystemCommandUtil {
    /**
     * 执行系统命令, 返回执行结果
     *
     * @param cmd 需要执行的命令
     */
    @SuppressWarnings("finally")
	public static String execCmd(String cmd) {
    	System.out.println(cmd);
        StringBuilder result = new StringBuilder();
        Process process = null;
        BufferedReader bufrIn = null;
        BufferedReader bufrError = null;
        try {
            String[] command = {"/bin/sh", "-c", cmd};
            // 执行命令, 返回一个子进程对象（命令在子进程中执行）
            process = Runtime.getRuntime().exec(command);
            // 方法阻塞, 等待命令执行完成（成功会返回0）
            process.waitFor();
            // 获取命令执行结果, 有两个结果: 正常的输出 和 错误的输出（PS: 子进程的输出就是主进程的输入）
            bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            bufrError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));
            // 读取输出
            String line;
            while ((line = bufrIn.readLine()) != null) {
                result.append(line);
            }
            while ((line = bufrError.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(bufrIn);
            closeStream(bufrError);
            // 销毁子进程
            if (process != null) {
                process.destroy();
            }
            // 返回执行结果
            return result.toString();
        }
    }

    /**
     * 关闭流
     *
     * @param stream
     */
    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送syslog
     *
     * @param stream
     */
    public static int sendSyslog(String ip, int port, String msg) {
    	SyslogIF syslog = Syslog.getInstance("udp");
        syslog.getConfig().setHost(ip);
        syslog.getConfig().setPort(port);
        try {
            syslog.log(SyslogConstants.LEVEL_CRITICAL, URLDecoder.decode(msg, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            return 0;
        }
        return 1;
    }
}
