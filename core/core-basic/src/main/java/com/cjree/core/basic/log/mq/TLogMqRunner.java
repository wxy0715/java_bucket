package com.cjree.core.basic.log.mq;

/**
 * mq的具体业务执行接口
 */
public interface TLogMqRunner<T> {

    void mqConsume(T t);

}
