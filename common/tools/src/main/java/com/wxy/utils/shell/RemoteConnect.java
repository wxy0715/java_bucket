package com.wxy.utils.shell;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 连接实体，ssh、mysql等等均可使用
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RemoteConnect {
    /**
     * 连接类型
     */
    private String type;
    /**
     * 连接ip
     */
    private String ip;
    /**
     * 连接用户名
     */
    private String userName;
    /**
     * 连接密码
     */
    private String password;
    /**
     * 连接端口
     */
    private int port;

}
