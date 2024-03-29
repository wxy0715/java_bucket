package com.wxy.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class MyClientHandler extends SimpleChannelInboundHandler<String> {

    private int count;
    @Override
    public void channelActive(ChannelHandlerContext ctx){
        //使用客户端发送10条数据 hello,server 编号
        for(int i= 0; i< 10; ++i) {
            ByteBuf buffer = Unpooled.copiedBuffer("hello,server " + i, StandardCharsets.UTF_8);
            ctx.writeAndFlush(buffer);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        //byte[] buffer = new byte[msg.readableBytes()];
        //msg.readBytes(buffer);
        //String message = new String(buffer, StandardCharsets.UTF_8);
        log.info("客户端接收到消息={}",msg);
        log.info("客户端接收到消息数量={}",++this.count);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}
