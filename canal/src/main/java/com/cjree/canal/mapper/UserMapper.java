package com.cjree.canal.mapper;

import com.cjree.canal.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;


public interface UserMapper extends BaseMapper<User> {

    Integer myCount();
}
