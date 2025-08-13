package com.cjree.core.basic.base;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cjree.core.model.common.Pagination;
import com.cjree.core.model.validate.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

/**
 * 数据库操作基础接口，所有的service实现类操作数据库必须走此接口
 */
@Validated
public interface BaseService<T extends com.cjree.core.basic.base.BaseModel> {

    /**
     * 查询实体集合（按主键集合）
     *
     * @param ids 主键集合
     * @return 实体集合
     */
    List<T> list(@NotNull List<@NotNull Long> ids);

    /**
     * 查询实体集合（按查询条件）
     * 注：调用该接口需实现 BaseMapper.selectIdPage() 方法
     *
     * @param query 实体（查询条件）
     * @return 实体集合
     */
    List<T> list(T query);

    /**
     * 查询实体集合（按查询条件）
     *
     * @param query 实体（查询条件）
     * @return 实体集合
     */
    List<T> listNonSql(T query);

    /**
     * 查询实体集合（按查询条件(wrapper方式)）
     *
     * @return wrapper wrapper入参
     */
    List<T> listNoSql(QueryWrapper<T> wrapper);

    List<Long> listIdsNoSql(QueryWrapper<T> wrapper);

    /**
     * 联合查询
     * 注：调用该接口需实现 BaseMapper.selectUnion() 方法
     *
     * @param query 实体（查询条件）
     * @return 实体集合（Map）
     */
    List<Map<String, Object>> unionList(T query);

    /**
     * 查询实体分页
     * 注：调用该接口需实现 BaseMapper.selectIdPage() 方法
     *
     * @param query      实体（查询条件）
     * @param pagination 分页实体（分页条件）
     * @return 分页实体
     */
    @Validated({Default.class, QueryPage.class})
    Pagination<T> page(T query, @NotNull @Valid Pagination<T> pagination);

    /**
     * 联合查询分页
     * 注：调用该接口需实现 BaseMapper.selectUnionPage() 方法
     *
     * @param query      实体（查询条件）
     * @param pagination 分页实体（分页条件）
     * @return 分页实体（Map）
     */
    @Validated({Default.class, QueryPage.class})
    Pagination<Map<String, Object>> unionPage(T query, @NotNull @Valid Pagination<T> pagination);

    /**
     * 查询实体（按主键）
     *
     * @param id 主键
     * @return 实体
     */
    T get(@NotNull Long id);

    /**
     * 查询实体（按查询条件）
     *
     * @param query 实体（查询条件）
     * @return 实体
     */
    @Validated({Default.class, Query.class})
    T get(@NotNull @Valid T query);

    /**
     * 插入实体
     *
     * @param entity 实体
     * @return 实体
     */
    @Validated({Insert.class})
    T insert(@NotNull @Valid T entity);

    /**
     * 插入实体（无返回）
     *
     * @param entity 实体
     */
    @Validated({Insert.class})
    void insertWithoutReturn(@NotNull @Valid T entity);

    /**
     * 批量插入实体
     *
     * @param entities 实体集合
     */
    @Validated({Insert.class})
    void insertBatch(List<@NotNull @Valid T> entities);

    /**
     * 修改实体
     *
     * @param entity 实体
     * @return 实体
     */
    @Validated({Update.class})
    T update(@NotNull @Valid T entity);

    /**
     * 根据条件修改
     *
     * @param updateWrapper 条件
     * @return 实体
     */
    void update(Wrapper<T> queryWrapper, Wrapper<T> updateWrapper);

    /**
     * 修改实体（无返回）
     *
     * @param entity 实体
     */
    @Validated({Update.class})
    void updateWithoutReturn(@NotNull @Valid T entity);

    /**
     * 批量修改实体
     *
     * @param entities 实体集合
     */
    @Validated({Update.class})
    void updateBatch(List<@NotNull @Valid T> entities);

    /**
     * 插入或修改实体
     *
     * @param entity 实体
     * @return 实体
     */
    T insertOrUpdate(@NotNull T entity);

    /**
     * 逻辑删除实体
     *
     * @param id 主键
     */
    void logicDelete(@NotNull Long id);

    /**
     * 批量逻辑删除实体
     *
     * @param ids 主键集合
     */
    void logicDeleteBatch(@NotNull List<@NotNull Long> ids);

    /**
     * 物理删除实体
     * 注：调用该接口需实现 BaseMapper.physicalDelete() 方法
     *
     * @param id 主键
     */
    @Validated({Delete.class})
    void physicalDelete(@NotNull Long id);

    /**
     * 批量物理删除实体
     */
    void physicalDeleteBatch(@NotNull List<@NotNull Long> ids);


    BaseMapper<T> getBaseMapper();

    default QueryChainWrapper<T> query() {
        return ChainWrappers.queryChain(this.getBaseMapper());
    }

    default LambdaQueryChainWrapper<T> lambdaQuery() {
        return ChainWrappers.lambdaQueryChain(this.getBaseMapper());
    }

    default UpdateChainWrapper<T> update() {
        return ChainWrappers.updateChain(this.getBaseMapper());
    }

    default LambdaUpdateChainWrapper<T> lambdaUpdate() {
        return ChainWrappers.lambdaUpdateChain(this.getBaseMapper());
    }

}
