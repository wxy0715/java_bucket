package com.wxy.canal.service.impl.user;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxy.canal.entity.User;
import com.wxy.canal.service.api.IUserService;
import com.wxy.canal.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wxy
 * @since 2023-01-06
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
