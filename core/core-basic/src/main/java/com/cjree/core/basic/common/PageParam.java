package com.cjree.core.basic.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class PageParam {

    @Schema(description = "分页参数：当前页")
    private Integer pageIndex;
    @Schema(description = "分页参数：页大小")
    private Integer pageSize;
    @Schema(description = "排序参数：顺序排序字段")
    private String asc;
    @Schema(description = "排序参数：逆序排序字段")
    private String desc;

}
