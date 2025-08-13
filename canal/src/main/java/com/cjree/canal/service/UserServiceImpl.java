package com.cjree.canal.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjree.canal.entity.User;
import com.cjree.canal.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
