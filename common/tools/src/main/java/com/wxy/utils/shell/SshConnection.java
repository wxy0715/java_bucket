package com.wxy.utils.shell;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * version 1.0
 * SSH连接到服务器执行操作
 * 官方文档:
 * http://www.ganymed.ethz.ch/ssh2/javadoc/ch/ethz/ssh2/package-summary.html
 */
@Slf4j
public class SshConnection {

    private static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.toString();

    private Connection connection;

    public SshConnection(RemoteConnect remoteConnect) {
        tryAcquire(remoteConnect);
    }

    /**
     * 初始化信息
     */
    static {

    }

    /**
     * 远程登录linux 服务器
     * @param remoteConnect 连接服务器属性
     */
    private void tryAcquire(RemoteConnect remoteConnect) {
        boolean flag;
        try {
            connection = new Connection(remoteConnect.getIp(), remoteConnect.getPort());
            connection.connect();
            flag = connection.authenticateWithPassword(remoteConnect.getUserName(), remoteConnect.getPassword());
            if (flag) {
                log.info("用户{}连接到{}成功！", remoteConnect.getUserName(), remoteConnect.getIp());
            } else {
                log.error("用户{}连接到{}失败！", remoteConnect.getUserName(), remoteConnect.getIp());
                connection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param cmd 脚本或者命令
     * @return String 命令执行成功后返回的结果值，如果命令执行失败，返回空字符串，不是null
     * @throws
     * @Title: execute
     * @Description: 远程执行shell脚本或者命令
     */
    public String execute(String cmd) {
        String result = "";
        try {
            Session session = connection.openSession();
            session.execCommand(cmd);// 执行命令
            result = processStdout(session.getStdout(), DEFAULT_CHARSET);
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 需要显式调用，必须手动调用
     */
    public void closeSsh() {
        connection.close();
    }

    public void copyFileTo(String path) {
        try {
            SCPClient scpClient = connection.createSCPClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param in      输入流对象
     * @param charset 编码
     * @return String 以纯文本的格式返回
     * @throws
     * @Title: processStdout
     * @Description: 解析脚本执行的返回结果
     */
    public String processStdout(InputStream in, String charset) {
        InputStream stdout = new StreamGobbler(in);
        StringBuilder buffer = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));
            String line;
            while ((line = br.readLine()) != null) {
                buffer.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public static void main(String[] args) {
        RemoteConnect connect = new RemoteConnect()
                .setIp("127.0.0.1")
                .setPort(22)
                .setUserName("root")
                .setPassword("root");
        SshConnection sshConnection = new SshConnection(connect);
        System.out.println(sshConnection.execute("route -n"));
    }

}
