package com.wxy.canal.config;

import com.alibaba.otter.canal.protocol.CanalEntry;

import javax.annotation.Resource;
import java.util.List;


public abstract class BaseAbstractStrategy<T> {

    @Resource
    TableContext tableContext;

    public abstract void syncInsert(T t) throws NoSuchFieldException;

    public abstract void syncUpdate(T t) throws NoSuchFieldException;

    public abstract void syncDelete(T t);


    public abstract T coverData(List<CanalEntry.Column> data) ;

}
