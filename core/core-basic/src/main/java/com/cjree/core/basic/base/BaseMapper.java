package com.cjree.core.basic.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 基础Mapper，所有的Mapper都继承此Mapper
 */
public interface BaseMapper<T extends BaseModel> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {

    List<IdModel> selectIdPage(@Param("cm") T params);

    List<IdModel> selectIdPage(@Param("cm") Map<String, Object> params);

    List<IdModel> selectIdPage(Page<Long> page, @Param("cm") Map<String, Object> params);

    List<IdModel> selectIdPage(Page<Long> page, @Param("cm") T params);

    List<T> selectPage(Page<Long> page, @Param("cm") Map<String, Object> params);

    List<T> selectPage(Page<Long> page, @Param("cm") T params);

    Integer selectCount(@Param("cm") Map<String, Object> params);

    Integer selectCount(@Param("cm") T params);

    List<Map<String, Object>> selectUnionPage(Page<Map<String, Object>> page, @Param("cm") Map<String, Object> param);

    List<Map<String, Object>> selectUnionPage(Page<Map<String, Object>> page, @Param("cm") T param);

    List<Map<String, Object>> selectUnion(@Param("cm") Map<String, Object> param);

    List<Map<String, Object>> selectUnion(@Param("cm") T param);

    Integer physicalDelete(Long id);

}
