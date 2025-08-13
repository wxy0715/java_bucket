package com.cjree.core.rabbitmq.constants;

public class RabbitConstants {

    /**
     * 默认死信交换机名称
     */
    public final static String EXCHANGE_DLX_DEFAULT = "exchange.dlx.default";

    /**
     * 默认死信队列名称
     */
    public static final String QUEUE_DLX_DEFAULT = "queue.dlx.default";

    /**
     * 默认死信队列绑定关系的RoutingKey
     */
    public static final String ROUTING_KEY_DLX_DEFAULT = "binding.routing.key.dlx.default";

    /**
     * 默认归档交换机名称
     */
    public final static String DEFAULT_DIRECT_EXCHANGE = "exchange.archived.default";

    /**
     * 默认归档队列名称
     */
    public static final String QUEUE_NAME = "queue.archived.default";

    /**
     * 默认归档队列绑定关系的RoutingKey
     */
    public static final String BINDING_ROUTING_KEY = "binding.routing.key.archived.default";
}
