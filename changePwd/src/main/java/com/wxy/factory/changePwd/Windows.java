package com.wxy.factory.changePwd;

import com.wxy.entity.ChangePwd;
import com.wxy.factory.AbstractChangePwd;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

@Slf4j
public class Windows extends AbstractChangePwd {
    public Windows(ChangePwd changePwd) {
        this.changePwd = changePwd;
    }

    @Override
    public boolean run() {
        // 建立socket
        try (Socket socket = new Socket(changePwd.getIp(), changePwd.getPort());
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ){
            String userInfo = changePwd.getUserName() + "|" + changePwd.getNewPassword() +"\n";
            log.info("发送信息为:{}",userInfo);
            writer.write(userInfo);
            writer.flush();
            String msg;
            StringBuilder error = new StringBuilder();
            while ((msg = reader.readLine()) != null) {
                error.append(msg);
                log.info("改密返回结果:{}",msg);
                if ("exit".equals(msg)) {
                    break;
                }
            }
            return error.toString().contains("成功");
        } catch (IOException e) {
            log.error("socker异常:",e);
            return false;
        }
    }

}
