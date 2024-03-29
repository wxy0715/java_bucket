package com.wxy.transactional.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author wxy
 * @since 2023-02-23
 */
@Getter
@Setter
public class Test implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    /**
     * 明文用户名
     */
    private String namePlain;

    /**
     * 加密用户名
     */
    private String nameEncrypt;

    /**
     * 是否是影子表
     */
    private Byte shadow;
}
