package com.cjree.core.canal;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class TableContext {

    private ConcurrentHashMap<String, BaseAbstractStrategy<?>> map = new ConcurrentHashMap<>();


    public void attach(String tableName, BaseAbstractStrategy<?> strategy) {
        map.put(tableName, strategy);
    }


    public BaseAbstractStrategy<?> getType(String tableName) {
        return map.get(tableName);
    }
}
