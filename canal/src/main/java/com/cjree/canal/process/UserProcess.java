package com.cjree.canal.process;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.cjree.canal.entity.User;
import com.cjree.core.canal.BaseAbstractStrategy;
import com.cjree.core.canal.CanalDataHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class UserProcess extends BaseAbstractStrategy<User> {

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
