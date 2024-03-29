package com.wxy.lock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wxy.lock.entity.MysqlLock;
import org.springframework.stereotype.Repository;


@Repository
public interface LockMapper extends BaseMapper<MysqlLock> {

}
