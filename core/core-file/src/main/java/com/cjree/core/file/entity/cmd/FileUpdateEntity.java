package com.cjree.core.file.entity.cmd;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

/**
 * 通过objectId更新附件
 */
@Data
public class FileUpdateEntity {
    /**
     * 文件id集合
     */
    private List<String> idList;

    /**
     * 文件所属对象id
     */
    @TableField(value = "object_id")
    private String objectId;
}
