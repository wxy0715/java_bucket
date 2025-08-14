package com.cjree.rabbitmq.server.controller;

import com.cjree.core.basic.log.mq.TLogMqConsumerProcessor;
import com.cjree.core.basic.log.mq.TLogMqRunner;
import com.cjree.core.basic.log.mq.TLogMqWrapBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RabbitListener(queues = "TestDirectQueue1")//监听的队列名称 TestDirectQueue
public class DirectReceiver1 {

    @RabbitHandler
    public void process(TLogMqWrapBean tLogMqWrapBean) {
        TLogMqConsumerProcessor.process(tLogMqWrapBean, (TLogMqRunner<Map>) o -> {
            //业务操作
            log.info("TestDirectQueue1消费者收到消息  : " + o.toString());
        });
    }

}