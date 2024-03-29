package com.wxy.factory.changePwd;

import com.wxy.entity.ChangePwd;
import com.wxy.factory.AbstractChangePwd;
import com.wxy.util.SshUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

@Slf4j
public class Linux extends AbstractChangePwd {

    public Linux(ChangePwd changePwd) {
        this.changePwd = changePwd;
        this.sshUtil = new SshUtil(changePwd);
        this.session = sshUtil.getConnection();
    }

    @Override
    public boolean run() {
        return changePwd();
    }

    /**普通用户改密*/
    public boolean changePwd() {
        String result = "";
        try (
                OutputStream stdin = session.getStdin();
                InputStream stderr = session.getStderr();
                InputStream stdout = session.getStdout()
                ){
            // 执行命令
            if (!changePwd.isAllowRoot()) {
                session.execCommand("passwd");
            } else {
                session.execCommand("passwd "+changePwd.getUserName());
            }
            if (!Objects.equals(changePwd.getUserName(), "root") && !changePwd.isAllowRoot()) {
                processStdin(stdin, DEFAULT_CHARSET, changePwd.getPassword());
            }
            processStdin(stdin,DEFAULT_CHARSET,changePwd.getNewPassword());
            processStdin(stdin,DEFAULT_CHARSET,changePwd.getNewPassword());
            result = processStdout(stdout, DEFAULT_CHARSET);
            log.info("输出结果为:{}",result);
            return result.contains(SUCCESSFUL) || result.contains(EN_SUCCESSFUL);
        } catch (Exception e) {
            log.error("改密异常",e);
            session.close();
            return false;
        }
    }

}
