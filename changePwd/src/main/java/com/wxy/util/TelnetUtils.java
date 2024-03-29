package com.wxy.util;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class TelnetUtils {
    /**结束标识字符串,Windows中是>,Linux中是#*/
    private String prompt = ">";
    /*** 结束标识字符*/
    private char promptChar = '>';
    /**TelnetClient对象*/
    private TelnetClient telnet;
    /** InputStream 输入流,接收返回信息*/
    private InputStream in;
    /**PrintStream 向服务器写入 命令*/
    private PrintStream out;

    /**
     * 构造函数
     * @param termtype
     * @param prompt
     */
    public TelnetUtils(String termtype,String prompt){
        this.telnet = new TelnetClient(termtype);
        setPrompt(prompt);
    }

    /**
     * 构造函数
     * @param termtype
     */
    public TelnetUtils(String termtype){
        this.telnet = new TelnetClient(termtype);
    }

    /**构造函数*/
    public TelnetUtils(){
        telnet = new TelnetClient();
    }

    /**
     * 登录到目标主机
     * @param ip Ip地址
     * @param port 端口号
     * @param username 用户名
     * @param password 密码
     */
    public void login(String ip, int port, String username, String password){
        try {
            telnet.connect(ip, port);
            in = telnet.getInputStream();
            out = new PrintStream(telnet.getOutputStream());
            readUntil("login:");
            write(username);
            readUntil("password:");
            write(password);
            String rs = readUntil(null);
            if(rs!=null&&rs.contains("Login Failed")){
                throw new RuntimeException("登录失败");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 读取分析结果
     * @param pattern 匹配到该字符串时返回结果
     * @return String
     */
    public String readUntil(String pattern) {
        StringBuffer sb = new StringBuffer();
        try {
            char lastChar = (char)-1;
            boolean flag = pattern!=null&&pattern.length()>0;
            if(flag) {
                lastChar = pattern.charAt(pattern.length() - 1);
            }
            char ch;
            int code = -1;
            while ((code = in.read()) != -1) {
                ch = (char)code;
                sb.append(ch);
                //匹配到结束标识时返回结果
                if (flag) {
                    if (ch == lastChar && sb.toString().endsWith(pattern)) {
                        return sb.toString();
                    }
                }else{
                    //如果没指定结束标识,匹配到默认结束标识字符时返回结果
                    if(ch == promptChar) {
                        return sb.toString();
                    }
                }
                //登录失败时返回结果
                if(sb.toString().contains("Login Failed")){
                    return sb.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 发送命令
     * @param value
     */
    public void write(String value) {
        try {
            out.println(value);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *功能说明:发送命令,返回执行结果
     *输入参数:command telnet命令
     *输出参数:String 命令执行结果
     * @throws UnsupportedEncodingException
     */
    public String sendCommand(String command) throws UnsupportedEncodingException {
        try {
            write(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //将返回结果转码为gbk,否则会乱码
        String serverResult = new String(readUntil(prompt).getBytes("ISO-8859-1"),"GBK") ;
        return serverResult;
    }

    /**
     *功能说明:关闭telnet连接
     *输入参数:
     *输出参数:
     */
    public void distinct(){
        try {
            if(telnet!=null&&!telnet.isConnected()) {
                telnet.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     *功能说明:设置结束标识符
     *输入参数:prompt 结束标识符
     *输出参数:
     */
    public void setPrompt(String prompt) {
        if(prompt!=null){
            this.prompt = prompt;
            this.promptChar = prompt.charAt(prompt.length()-1);
        }
    }

    /**
     *功能说明:测试函数
     *输入参数:args
     *输出参数:
     * @throws UnsupportedEncodingException
     */
    public String excuteWinCmd(String remoteHost,String port,String userName,String passWord,String winCmd) throws UnsupportedEncodingException {
        login(remoteHost, Integer.valueOf(port), userName, passWord);
        String serverResult = sendCommand(winCmd);
        distinct();
        return serverResult;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        //Windows,用VT220,否则会乱码
        TelnetUtils telnetUtils = new TelnetUtils("VT220",">");
        String result = telnetUtils.excuteWinCmd("127.0.0.1", "23", "administrator", "wxy0715..","net user");
        System.out.println(result);
    }
}
