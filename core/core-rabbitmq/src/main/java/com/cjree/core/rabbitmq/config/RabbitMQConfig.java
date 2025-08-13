package com.cjree.core.rabbitmq.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    /**
     * 使用Template进行操作
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        // 若传递数据时不想手动将对象序列化为其他格式，那么我们可以配置序列化处理类来进行处理
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        //当 mandatory 参数设为 true 时，交换器无法根据自身的 类型和路由键找到一个符合条件 的队列，那么 RabbitMQ 会调用 Basic.Return 命令将消息返回给生产者 。当 mandatory 参 数设置为 false 时，出现上述情形，则消息直接被丢弃 。
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            System.out.println("ConfirmCallback:     "+"相关数据："+correlationData);
            System.out.println("ConfirmCallback:     "+"确认情况："+ack);
            System.out.println("ConfirmCallback:     "+"原因："+cause);
        });
        rabbitTemplate.setReturnsCallback((ack) -> {
            System.out.println("ReturnCallback:     "+"消息："+ack.getMessage());
            System.out.println("ReturnCallback:     "+"回应码："+ack.getReplyCode());
            System.out.println("ReturnCallback:     "+"回应信息："+ack.getReplyText());
            System.out.println("ReturnCallback:     "+"交换机："+ack.getExchange());
            System.out.println("ReturnCallback:     "+"路由键："+ack.getRoutingKey());
        });
        return rabbitTemplate;
    }

    /**
     * 配置消息类型转化器
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

