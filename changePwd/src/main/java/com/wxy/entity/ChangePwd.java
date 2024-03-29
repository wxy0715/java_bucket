package com.wxy.entity;

import com.wxy.factory.ChangePwdEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class ChangePwd {
    /**改密类型枚举*/
    private ChangePwdEnum dataBaseEnum;
    /**ip*/
    private String ip;
    /**用户名*/
    private String userName;
    /**旧密码*/
    private String password;
    /**新密码*/
    private String newPassword;
    /**端口*/
    private int port;

    /**root账号*/
    private boolean allowRoot;
    /**超级账号*/
    private String superAccount;
    /**超级密码*/
    private String superPassword;
}
