package com.wxy.lock.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("mysql_lock")
public class MysqlLock {

    @TableField("id")
    private Long id;

    @TableField("mylock")
    private String mylock;
}
