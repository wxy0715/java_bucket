package com.cjree.seata1.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cjree.core.basic.base.AbstractCacheableModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "ExamplePo", description = "示例表")
@TableName(value = "t_example", autoResultMap = true)
public class ExamplePo extends AbstractCacheableModel {

    @Schema(description = "code")
    @TableField("code")
    private String code;

    @Schema(description = "name")
    @TableField("name")
    private String name;

    @Schema(description = "person_name")
    @TableField("person_name")
    private String personName;

    @Schema(description = "ic_code")
    @TableField("ic_code")
    private String icCode;
}
