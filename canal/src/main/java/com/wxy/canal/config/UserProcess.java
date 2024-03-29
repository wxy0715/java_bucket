package com.wxy.canal.config;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.wxy.canal.entity.User;
import com.wxy.canal.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author 季小意
 */
@Component
@Slf4j
public class UserProcess extends BaseAbstractStrategy<User>{

    @Resource
    private UserMapper userMapper;

    @PostConstruct
    private void init(){
        tableContext.attach("user", this);
        tableContext.attach("user_bak", this);
    }

    @Override
    public void syncInsert(User user) {
        syncUpdate(user);
    }

    @Override
    public void syncUpdate(User user) {
        log.info("syncUpdate user value:{}", JSONObject.toJSONString(user));
    }

    @Override
    public void syncDelete(User user) {
        log.info("delete user value:{}", JSONObject.toJSONString(user));
    }

    @Override
    public User coverData(List<CanalEntry.Column> data) {
        return CanalDataHandler.convertToBean(data, User.class);
    }
}
