package com.cjree.core.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.cjree.core.model.common.Pagination;
import com.cjree.core.model.enums.Available;
import com.cjree.core.model.validate.Delete;
import com.cjree.core.model.validate.QueryById;
import com.cjree.core.model.validate.QueryPage;
import com.cjree.core.model.validate.Update;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * 基础Model，所有的model都要继承此model
 */

@Data
@Slf4j
public abstract class BaseCoreModel implements Serializable {

    @NotNull(groups = {QueryById.class, Update.class, Delete.class}, message = "主键不能为空")
    @Schema(description = "主键", example = "1")
    @TableId(value = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    protected Long id;

    @Schema(description = "是否可用", example = "YES")
    @TableField("available")
    protected Available available;

    @Schema(description = "版本时间戳", example = "2025-03-26 01:11:16.150")
    @TableField("version_date")
    protected Date versionDate;

    @Schema(description = "更新日期", hidden = true)
    @TableField("update_date")
    protected Date updateDate;

    @Schema(description = "更新人", hidden = true)
    @TableField("updater")
    protected Long updater;

    @Schema(description = "创建日期", hidden = true)
    @TableField("create_date")
    protected Date createDate;

    @Schema(description = "创建人", hidden = true)
    @TableField("creator")
    protected Long creator;

    @Schema(description = "备注", hidden = true)
    @TableField("remark")
    protected String remark;

    @NotNull(groups = {QueryPage.class}, message = "当前页不能为空")
    @Min(value = 1, message = "当前页不能小于1")
    @Transient
    @Schema(description = "分页参数：当前页", example = "1")
    @TableField(exist = false)
    private Integer pageIndex;

    @NotNull(groups = {QueryPage.class}, message = "每页显示条数不能为空")
    @Min(value = 1, message = "每页显示条数不能小于1")
    @Transient
    @Schema(description = "分页参数：页大小", example = "10")
    @TableField(exist = false)
    private Integer pageSize;

    @Transient
    @Schema(description = "排序参数：顺序排序字段", example = "available,id")
    @TableField(exist = false)
    private String asc;

    @Transient
    @Schema(description = "排序参数：逆序排序字段", example = "creator,updator")
    @TableField(exist = false)
    private String desc;

    @Transient
    @Schema(description = "排序参数：组装好的排序字段", hidden = true)
    @TableField(exist = false)
    private String orderBy;

    public BaseCoreModel id(Long id) {
        this.setId(id);
        return this;
    }

    public BaseCoreModel available(Available available) {
        this.setAvailable(available);
        return this;
    }

    /**
     * 构造必须的where条件，主键和版本时间戳缺一不可
     */
    @SneakyThrows
    public BaseCoreModel where() {
        BaseCoreModel m = this.getClass().newInstance();
        m.setId(this.getId());
        if (!Objects.isNull(this.getVersionDate())) {
            m.setVersionDate(this.getVersionDate());
        }
        return m;
    }

    public Pagination createPagination() {
        Pagination pagination = new Pagination();
        if (StringUtils.isNotEmpty(this.getAsc())) {
            pagination.setAscs(Arrays.asList(this.getAsc().split(",")));
        }
        if (StringUtils.isNotEmpty(this.getDesc())) {
            pagination.setDescs(Arrays.asList(this.getDesc().split(",")));
        }
        pagination.setCurrent(this.getPageIndex());
        pagination.setSize(this.getPageSize());
        return pagination;
    }
}
