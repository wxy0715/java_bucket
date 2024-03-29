package com.wxy.utils.shell;

import com.jcraft.jsch.JSchException;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

/**
 * create by wsw
 * ssh连接的工具类
 * 使用说明
 *  newInstance()创建session实例
 *  exec***()执行命令
 *  closeSession()
 *
 */
@Slf4j
public class SshExecuter implements Closeable{

    public SshExecuter(){ }
    /**
     * 使用java给Linux发送命令，接受返回的消息
     * @param cmd 要运行的命令
     * @return
     */
    public String execToString(String cmd) {
       return execShReturnString(cmd);
    }
    /**
     * 使用java给Linux发送命令，接受返回的消息
     * @param cmd 要运行的命令
     * @return
     */
    public List<String> execToList(String cmd) {
        return execShReturnList(cmd);
    }




    /**
     * 执行一条后台命令(无返回值)
     * @param cmd
     * @throws Exception
     */
    public void exec_nohup( String cmd ) throws JSchException,IOException{
      execShNohup(cmd);
    }

    /**
     * 执行一条命令
     * @param cmd
     * @return
     * @throws Exception
     */
    public Boolean execToBoolean( String cmd ) throws JSchException,IOException{
       return execShReturnBoolean(cmd);
    }

    /**
     * 执行一个sh命令
     *
     * @param shell
     * @return
     */
    public static String execSh(String shell) throws Exception{
        return execShReturnString(shell);
    }

    /**
     * 关闭连接
     * @throws IOException
     */
    @Override
    public void close()  throws IOException{
        //空方法
    }

    /**
     * 执行一个sh命令
     * @param shell
     * @return
     */
    public static void  execShNohup(String shell) {
        try {
            log.info("执行execShNohup:"+shell);
            // 后台执行
            shell = "nohup "+ shell +" >/dev/null 2>&1 &";
            Runtime.getRuntime().exec(new String[] {"/bin/sh","-c",shell},null,null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    /**
     * 执行一个sh命令
     * @param shell
     * @return
     */
    public static Boolean  execShReturnBoolean(String shell) {
        try {
            log.info("执行execShReturnBoolean:"+shell);
            Process process = Runtime.getRuntime().exec(new String[] {"/bin/sh","-c",shell},null,null);
            int res=process.waitFor();
            if(res==0) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return false;
    }


    /**
     * 执行一个sh命令
     * @param shell
     * @return
     */
    public static String execShReturnString(String shell) {
        log.info("执行execShReturnString:"+shell);
        StringBuffer sb = new StringBuffer();
        List<String> list=execShReturnList(shell);
        if(list!=null && !list.isEmpty()){
            for(String s:list){
                sb.append(s);
                sb.append(";");
            }
        }
        return sb.toString();
    }

    /**
     * 执行一个sh命令
     * @param shell
     * @return
     */
    public static List<String> execShReturnList(String shell) {
        log.info("执行execShReturnList:"+shell);
        InputStreamReader isr =null;
        LineNumberReader lnr=null;
        List<String> list=new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec(new String[] {"/bin/sh","-c",shell},null,null);
            isr = new InputStreamReader(process.getInputStream());
            lnr = new LineNumberReader(isr);
            String line;
            process.waitFor();
            while ((line = lnr.readLine()) != null) {
                list.add(line);
            }
            return  list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }finally {
            try {
                if(isr!=null)isr.close();
            }catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            try {
                if(lnr!=null)lnr.close();
            }catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
