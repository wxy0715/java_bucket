package com.cjree.core.file.entity.cmd;

import lombok.Data;

import java.util.List;

/**
 * 查询文件集合
 */
@Data
public class QueryFileEntity {
    /**
     * 文件id集合
     */
    private List<String> objectIdList;

    /**
     * 文件所属对象id
     */
    private String objectType;
}
