package com.cjree.core.canal;

import com.alibaba.otter.canal.protocol.CanalEntry;
import jakarta.annotation.Resource;

import java.util.List;


public abstract class BaseAbstractStrategy<T> {

    @Resource
    public TableContext tableContext;

    public abstract void syncInsert(T t) throws NoSuchFieldException;

    public abstract void syncUpdate(T t) throws NoSuchFieldException;

    public abstract void syncDelete(T t);


    public abstract T coverData(List<CanalEntry.Column> data) ;

}
