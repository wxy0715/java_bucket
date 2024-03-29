package com.wxy.canal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@TableName(value = "user", autoResultMap = true)
@Data
@Accessors(chain = true)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String username;
    private String phone;
    private String password;
    private String email;


}
