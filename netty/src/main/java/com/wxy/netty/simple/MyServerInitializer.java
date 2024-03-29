package com.wxy.netty.simple;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;


public class MyServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());
        /*
        1. IdleStateHandler 是netty 提供的处理空闲状态的处理器
        2. long readerIdleTime : 表示多长时间没有读, 就会发送一个心跳检测包检测是否连接
        3. long writerIdleTime : 表示多长时间没有写, 就会发送一个心跳检测包检测是否连接
        4. long allIdleTime : 表示多长时间没有读写, 就会发送一个心跳检测包检测是否连接
        5. 文档说明
            triggers an {@link IdleStateEvent} when a {@link Channel} has not performed
            read, write, or both operation for a while.
        6. 当 IdleStateEvent 触发后 , 就会传递给管道 的下一个handler去处理
           通过调用(触发)下一个handler 的 userEventTiggered , 在该方法中去处理 IdleStateEvent(读空闲，写空闲，读写空闲)
        */
        pipeline.addLast(new IdleStateHandler(7000,7000,10, TimeUnit.SECONDS));
        pipeline.addLast(new MyServerHandler());
    }
}
