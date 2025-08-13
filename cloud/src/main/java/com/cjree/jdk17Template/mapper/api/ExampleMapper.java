package com.cjree.jdk17Template.mapper.api;

import com.cjree.core.basic.base.BaseMapper;
import com.cjree.jdk17Template.po.ExamplePo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExampleMapper extends BaseMapper<ExamplePo> {
}
