package com.wxy.netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MyClient {
    public static void main(String[] args) throws Exception{
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        try {
            bootstrap.group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .handler(new MyClientInitializer());
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1",7001).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            eventExecutors.shutdownGracefully();
        }
    }
}
