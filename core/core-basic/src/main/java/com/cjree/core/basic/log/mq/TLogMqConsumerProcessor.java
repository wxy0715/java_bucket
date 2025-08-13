package com.cjree.core.basic.log.mq;


import com.cjree.core.basic.log.rpc.TLogRPCHandler;

/**
 * tlog的mq消费者处理器
 */
public class TLogMqConsumerProcessor {

    private static TLogRPCHandler tLogRPCHandler = new TLogRPCHandler();

    public static void process(TLogMqWrapBean tLogMqWrapBean, TLogMqRunner tLogMqRunner){
        tLogRPCHandler.processProviderSide(tLogMqWrapBean);
        try{
            tLogMqRunner.mqConsume(tLogMqWrapBean.getT());
        }finally {
            tLogRPCHandler.cleanThreadLocal();
        }
    }
}
