package com.wxy.easyes.document;

import lombok.Data;
import org.dromara.easyes.annotation.HighLight;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.Score;
import org.dromara.easyes.annotation.rely.Analyzer;
import org.dromara.easyes.annotation.rely.FieldType;

import java.math.BigDecimal;

/**
 * 文档实体
 */
@Data
public class Document {

    /**
     * es中的唯一id
     */
    private Long id;

    private BigDecimal money;

    private Integer type;

    /**
     * 文档标题
     */
    @HighLight
    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.IK_SMART)
    private String title;

    /**
     * 文档内容,指定了类型及存储/查询分词器
     */
    @HighLight(mappingField = "highlightContent")
    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.IK_SMART)
    private String content;

    /**
     * 高亮返回值被映射的字段
     */
    private String highlightContent;

    /**
     * 得分
     */
    @Score
    private Float scope;

    @IndexField(fieldType = FieldType.IP)
    private String ipAddress;
}
