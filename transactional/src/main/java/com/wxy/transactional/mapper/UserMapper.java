package com.wxy.transactional.mapper;

import com.wxy.transactional.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wxy
 * @since 2023-01-06
 */
public interface UserMapper extends BaseMapper<User> {

    Integer myCount();

    Integer saveBatch(@Param("list") List<User> list);
}
