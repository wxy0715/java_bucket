package com.wxy.core;

import com.wxy.entity.ChangePwd;
import com.wxy.factory.ChangePwdEnum;
import com.wxy.factory.ChangePwdFactory;
import com.wxy.factory.IChangePwd;
import com.wxy.util.GenPwdUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wxy
 * @description  linux unix  kylin 改密
 * @create 2021/10/28 16:52
 */
@Slf4j
public class LinuxTest {
    public static void main(String[] args) {
        String newPwd = GenPwdUtil.generatorPwdByLen(8);
        log.info("新密码为【{}】",newPwd);
        ChangePwd changePwd = ChangePwd.builder()
                .ip("49.232.142.65").userName("root").password("iV4=FK11").port(12288)
                .dataBaseEnum(ChangePwdEnum.LINUX).newPassword(newPwd)
                .allowRoot(false).superPassword("qqwang0715..").superAccount("root")
                .build();
        // 获取改密类型
        ChangePwdFactory changePwdFactory = new ChangePwdFactory();
        IChangePwd iChangePwd = changePwdFactory.createChangePwd(changePwd);
        boolean b = iChangePwd.run();
        log.info("改密结果:{}",b);
    }
}
