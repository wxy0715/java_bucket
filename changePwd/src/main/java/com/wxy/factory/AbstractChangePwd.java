package com.wxy.factory;

import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.wxy.entity.ChangePwd;
import com.wxy.util.SshUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
public class AbstractChangePwd implements IChangePwd{

    protected ChangePwd changePwd;
    protected SshUtil sshUtil;
    protected Session session;

    protected static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.toString();
    protected static final String SUCCESSFUL = "成功";
    protected static final String EN_SUCCESSFUL = "success";
    protected static final String FAILED = "'失败'";
    protected static final String EN_FAILED = "Error";
    protected static final String EN_FAIL_HUA3C = "Cannot change password";
    protected static final String EN_FAIL_HUA3C1 = "Permission denied";

    /**
     * 普通用户改密
     */
    @Override
    public boolean run() {
        return false;
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

    /**
     * @param out      输出流对象
     * @param charset 编码
     * @return String 以纯文本的格式返回
     * @Title: processStdout
     * @Description: 执行结果后的输入
     */
    public void processStdin(OutputStream out, String charset, String cmd) {
        cmd = cmd +"\n";
        try {
            out.write(cmd.getBytes());
        } catch (Exception e) {
            log.error("字符转换异常:",e);
        }
    }

    /**
     * @param stderr      输出流对象
     * @param charset 编码
     * @return String 以纯文本的格式返回
     * @Title: processStdout
     * @Description: 执行结果后的错误
     */
    public String processStderr(InputStream stderr, String charset) {
        InputStream stdout = new StreamGobbler(stderr);
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
}
