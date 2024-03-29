package com.wxy.lock.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@TableName("stock")
public class Stock {

    @TableField("id")
    private Long id;

    private Long productCode;

    private Integer count;

    private Integer version;
}
