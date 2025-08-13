package com.cjree.core.rabbitmq.utils;

import com.alibaba.fastjson.JSON;
import com.cjree.core.basic.log.mq.TLogMqConsumerProcessor;
import com.cjree.core.basic.log.mq.TLogMqRunner;
import com.cjree.core.basic.log.mq.TLogMqWrapBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Component
public class RabbitMqUtils {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息（自动包装 TraceID）
     * @param exchange 交换机
     * @param routingKey 路由键
     * @param payload 消息体
     */
    public <T> void send(String exchange, String routingKey, T payload) {
        TLogMqWrapBean<T> wrappedMsg = new TLogMqWrapBean<>(payload);
        rabbitTemplate.convertAndSend(exchange, routingKey, wrappedMsg);
    }


    /**
     * 接收消息
     * @param tLogMqWrapBean 消息体
     * @param businessLogic 业务逻辑
     */
    public <T> void process(TLogMqWrapBean<T> tLogMqWrapBean, Consumer<T> businessLogic) {
        TLogMqConsumerProcessor.process(tLogMqWrapBean, (TLogMqRunner<T>) runner ->{
            businessLogic.accept(tLogMqWrapBean.getT());
        });
    }


    /**
     * 接收消息
     * @param message 消息体
     * @param clazz 消息类型
     * @param businessLogic 业务逻辑
     */
    public <T> void process(Message message, Class<T> clazz, Consumer<T> businessLogic) {
        // 将Consumer适配为Function
        Function<T, Void> adapter = t -> {
            businessLogic.accept(t);
            return null;
        };
        processWithResult(message, clazz, adapter);
    }

    /**
     * 有返回值的消费者处理
     * @param message 消息体
     * @param clazz 消息类型
     * @param businessLogic 业务逻辑
     */
    public <T, R> R processWithResult(Message message, Class<T> clazz, Function<T, R> businessLogic) {
        // 提取消息体
        TLogMqWrapBean<T> wrapBean = extractMessageBody(message, clazz);
        // 创建支持返回值的适配器
        ResultAwareRunner<T, R> runner = new ResultAwareRunner<>(businessLogic);
        // 执行TLog处理流程
        TLogMqConsumerProcessor.process(wrapBean, runner);
        return runner.getResult();

    }

    // 支持返回值处理的适配器
    private static class ResultAwareRunner<T, R> implements TLogMqRunner<T> {
        private final Function<T, R> businessLogic;
        private R result;

        public ResultAwareRunner(Function<T, R> businessLogic) {
            this.businessLogic = businessLogic;
        }

        @Override
        public void mqConsume(T t) {
            result = businessLogic.apply(t);
        }

        public R getResult() {
            return result;
        }
    }

    // 消息体提取（增强类型安全）
    // TODO 试图根据泛型传递推断类型,但lambda表达式存在泛型擦除,此处无法获取消息体类型,暂定使用Class传入类型参数
    @SuppressWarnings("unchecked")
    private <T> TLogMqWrapBean<T> extractMessageBody(Message message, Class<T> clazz) {
        // 1. 首先尝试使用Spring的消息转换器
        Object body = rabbitTemplate.getMessageConverter().fromMessage(message);
        if (body instanceof TLogMqWrapBean) {
            TLogMqWrapBean<T> wrapBean = (TLogMqWrapBean<T>) body;

            T convertedObj = JSON.parseObject(JSON.toJSONString(wrapBean.getT()), clazz);
            wrapBean.setT(convertedObj);
            return wrapBean;
        }
        throw new IllegalArgumentException("消息类型必须是TLogMqWrapBean");
    }

    // 检查对象是否已经是正确类型
    private <T> boolean isCorrectType(Object obj, Object businessLogic) {
        if (obj == null) return true;
        Class<?> targetType = getTargetType(businessLogic);
        return targetType != null && targetType.isInstance(obj);
    }

    // 获取目标类型（从业务逻辑推断）
    private Class<?> getTargetType(Object businessLogic) {
        if (businessLogic instanceof Consumer) {
            return getGenericType((Consumer<?>) businessLogic);
        } else if (businessLogic instanceof Function) {
            return getGenericType((Function<?, ?>) businessLogic);
        }
        return null;
    }

    // 从Consumer获取泛型类型
    private Class<?> getGenericType(Consumer<?> consumer) {
        Type[] genericTypes = ((ParameterizedType) consumer.getClass()
                .getGenericInterfaces()[0]).getActualTypeArguments();

        if (genericTypes.length > 0 && genericTypes[0] instanceof Class) {
            return (Class<?>) genericTypes[0];
        }
        return Object.class;
    }

    // 从Function获取泛型类型
    private Class<?> getGenericType(Function<?, ?> function) {
        Type[] genericTypes = ((ParameterizedType) function.getClass()
                .getGenericInterfaces()[0]).getActualTypeArguments();

        if (genericTypes.length > 0 && genericTypes[0] instanceof Class) {
            return (Class<?>) genericTypes[0];
        }
        return Object.class;
    }
}
