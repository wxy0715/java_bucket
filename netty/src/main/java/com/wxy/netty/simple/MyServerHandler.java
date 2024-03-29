package com.wxy.netty.simple;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
public class MyServerHandler extends SimpleChannelInboundHandler<String> {
    private int count;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    //读取数据实际(这里我们可以读取客户端发送的消息)
    /*
    1. ChannelHandlerContext ctx:上下文对象, 含有 管道pipeline , 通道channel, 地址
    2. Object msg: 就是客户端发送的数据 默认Object
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
//        if(msg instanceof IdleStateEvent) {
//            //将  evt 向下转型 IdleStateEvent
//            IdleStateEvent event = (IdleStateEvent) msg;
//            String eventType = null;
//            switch (event.state()) {
//                case READER_IDLE:
//                    eventType = "读空闲";
//                    break;
//                case WRITER_IDLE:
//                    eventType = "写空闲";
//                    break;
//                case ALL_IDLE:
//                    eventType = "读写空闲";
//                    break;
//            }
//            System.out.println(ctx.channel().remoteAddress() + "--超时时间--" + eventType);
//        }

       // byte[] buffer = new byte[msg.readableBytes()];
        //String message = new String(buffer, StandardCharsets.UTF_8);
        log.info("服务器接收到数据= {}",msg);
        log.info("服务器接收到消息量={}",++this.count);
        //服务器回送数据给客户端, 回送一个随机id ,
        ByteBuf responseByteBuf = Unpooled.copiedBuffer(UUID.randomUUID() + " ", StandardCharsets.UTF_8);
        ctx.writeAndFlush(responseByteBuf);
    }
}
