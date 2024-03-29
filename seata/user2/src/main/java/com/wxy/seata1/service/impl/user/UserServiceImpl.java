package com.wxy.seata1.service.impl.user;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxy.seata1.entity.User;
import com.wxy.seata1.enums.AgeEnum;
import com.wxy.seata1.mapper.UserMapper;
import com.wxy.seata1.service.api.IUserService;
import io.seata.core.context.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionManager;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wxy
 * @since 2023-01-06
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    private static final Logger log = LoggerFactory.getLogger(Class.class);
    @Resource
    private DataSourceTransactionManager transactionManager;

    @Override
    public void save(){
        String xid = RootContext.getXID();

        log.info("分布式xid为:{}",xid);
        User user = new User();
        user.setAge(AgeEnum.THREE);
        user.setName("2");
        baseMapper.insert(user);
        log.info("保存测试2成功");
        int i = 10/0;
    }
}
