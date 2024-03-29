package com.wxy.shardingjdbc.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author wxy
 * @since 2023-01-07
 */
@Getter
@Setter
@TableName("user_detail")
public class UserDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long userId;

    private String age;

    private String sex;
}
