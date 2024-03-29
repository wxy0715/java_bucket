package com.wxy.seata.config;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.wxy.seata.entity.Wallet;

import java.io.IOException;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 自定义复杂类型处理器<br/>
 * 顶层父类是无法获取到准确的待转换复杂返回类型数据
 */
public class WalletListTypeHandler extends JacksonTypeHandler {

    public WalletListTypeHandler(Class<?> type) {
        super(type);
    }

    @Override
    protected Object parse(String json) {
        try {
            return getObjectMapper().readValue(json, new TypeReference<List<Wallet>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
