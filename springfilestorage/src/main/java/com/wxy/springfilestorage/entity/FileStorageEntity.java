package com.wxy.springfilestorage.entity;

import lombok.Data;

/**
 * 文件存储实体
 * @author wangxingyu
 * @date 2023/01/28
 */
@Data
public class FileStorageEntity {
    /**
     * 路径
     */
    private String path = "/";
    /**
     * 对象id
     */
    private String objectId = null;
    /**
     * 对象类型
     */
    private String objectType = "";
}
