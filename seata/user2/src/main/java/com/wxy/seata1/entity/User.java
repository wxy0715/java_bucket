package com.wxy.seata1.entity;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.wxy.seata1.enums.AgeEnum;
import com.wxy.seata1.enums.SexEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author wxy
 * @since 2023-01-06
 */
@TableName(value = "user", autoResultMap = true)
@Data
@Accessors(chain = true)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    @JSONField(serialzeFeatures= SerializerFeature.WriteEnumUsingToString)
    private AgeEnum age;

    /**
     * 性别
     */
    @JSONField(serialzeFeatures= SerializerFeature.WriteEnumUsingToString)
    private SexEnum sex;


    //@TableField(typeHandler = WalletListTypeHandler.class)
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Wallet> wallets;

    /**
     * 注意！！ 必须开启映射注解
     * @TableName(autoResultMap = true)
     * 以下两种类型处理器，二选一 也可以同时存在
     * 注意！！选择对应的 JSON 处理器也必须存在对应依赖包
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    // @TableField(typeHandler = JacksonTypeHandler.class)
    private OtherInfo otherInfo;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 逻辑删除字段
     */
    private String available;

    /**
     * 逻辑删除字段
     */
    @Version
    private Integer version;

    /**
     * 租户字段
     */
    public String tenantId;
}
