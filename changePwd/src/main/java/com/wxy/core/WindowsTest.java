package com.wxy.core;

import com.wxy.entity.ChangePwd;
import com.wxy.factory.ChangePwdEnum;
import com.wxy.factory.ChangePwdFactory;
import com.wxy.factory.IChangePwd;
import com.wxy.util.GenPwdUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wxy
 * @description  windows改密客户端
 * 查看用户 net user
 * 创建用户命令 net user test wxy0715@@ /add
 * 删除 net user test /delete
 * 修改 net user test wxy0715@@
 * 端口按需要指定,需要开防火墙
 * 开机自启exe C:\ProgramData\Microsoft\Windows\Start Menu\Programs\StartUp  把exe的快捷方式粘贴到此目录下即可
 * @create 2021/10/28 16:52
 */
@Slf4j
public class WindowsTest {
    public static void main(String[] args) {
        String newPwd = GenPwdUtil.generatorPwdByLen(8);
        log.info("新密码为【{}】",newPwd);
        ChangePwd changePwd = ChangePwd.builder()
                .ip("49.232.142.65").userName("test").password("").port(44444)
                .dataBaseEnum(ChangePwdEnum.WINDOWS).newPassword(newPwd)
                .allowRoot(false).superPassword("qqwang0715..").superAccount("root")
                .build();
        // 获取改密类型
        ChangePwdFactory changePwdFactory = new ChangePwdFactory();
        IChangePwd iChangePwd = changePwdFactory.createChangePwd(changePwd);
        boolean b = iChangePwd.run();
        log.info("改密结果:{}",b);
    }
}
