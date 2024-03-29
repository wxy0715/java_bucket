package com.wxy.util;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.wxy.entity.ChangePwd;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;

/** 官方文档: http://www.ganymed.ethz.ch/ssh2/javadoc/ch/ethz/ssh2/package-summary.html*/
@Slf4j
public class SshUtil {
    private static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.toString();
    private Connection connection;

    public SshUtil(ChangePwd changePwd){
        instance(changePwd);
    }

    private void instance(ChangePwd changePwd) {
        boolean flag;
        try {
            connection = new Connection(changePwd.getIp(), changePwd.getPort());
            connection.connect();
            if (changePwd.isAllowRoot()){
                flag = connection.authenticateWithPassword(changePwd.getSuperAccount(), changePwd.getSuperPassword());
            } else {
                flag = connection.authenticateWithPassword(changePwd.getUserName(), changePwd.getPassword());
            }
            if (flag) {
                log.info("用户{}连接到{}成功!", changePwd.isAllowRoot()?changePwd.getSuperAccount():changePwd.getUserName(), changePwd.getIp());
            } else {
                log.error("用户{}连接到{}失败!", changePwd.isAllowRoot()?changePwd.getSuperAccount():changePwd.getUserName(), changePwd.getIp());
                connection.close();
            }
        } catch (IOException e) {
            log.error("用户{}连接到{}异常!,异常为:{}", changePwd.getUserName(), changePwd.getIp(),e);
        }
    }

    public Session getConnection(){
        try {
            return connection.openSession();
        } catch (IOException e) {
            log.error("获取shh->session失败",e);
            // todo 抛异常 或者统一结果去处理,暂时不优化
            return null;
        }
    }
    /**
     * @param cmd 脚本或者命令
     * @return String 命令执行成功后返回的结果值，如果命令执行失败，返回空字符串，不是null
     */
    public String execute(String cmd) {
        String result = "";
        try {
            Session session = connection.openSession();
            session.execCommand(cmd);
            result = processStdout(session.getStdout(), DEFAULT_CHARSET);
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param in      输入流对象
     * @param charset 编码
     * @return String 以纯文本的格式返回
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

    /**关闭ssh*/
    public void closeSsh() {
        connection.close();
    }

}
