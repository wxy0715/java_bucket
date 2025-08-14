package com.cjree.shardingjdbc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjree.shardingjdbc.model.User;
import com.cjree.shardingjdbc.model.UserDetail;

import java.util.List;

public interface UserDetailMapper extends BaseMapper<UserDetail> {

    List<User> inner();
}
