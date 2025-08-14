
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
@RabbitListener(queues = "fanout.C")
@Slf4j
public class FanoutReceiverC {

    @RabbitHandler
    public void process(TLogMqWrapBean tLogMqWrapBean) {
        TLogMqConsumerProcessor.process(tLogMqWrapBean, (TLogMqRunner<Map>) o -> {
            //业务操作
            log.info("FanoutReceiverC消费者收到消息  : " + o.toString());
        });
    }
}