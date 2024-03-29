package com.wxy.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * netty服务端
 * @author wangxingyu
 * @date 2023/09/14 16:08:50
 */
public class MyServer {
    public static void main(String[] args) throws Exception{
        //创建BossGroup 和 WorkerGroup
        //说明
        //1. 创建两个线程组 bossGroup 和 workerGroup
        //2. bossGroup 只是处理连接请求 , 真正的和客户端业务处理，会交给 workerGroup完成
        //3. 两个都是无限循环
        //4. bossGroup 和 workerGroup 含有的子线程(NioEventLoop)的个数
        //   默认实际 cpu核数 * 2
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup childGroup = new NioEventLoopGroup(1);
        try{
            //创建服务器端的启动对象，配置参数
            ServerBootstrap serverBootstrap  = new ServerBootstrap();
            //使用链式编程来进行设置,设置两个线程组
            serverBootstrap.group(bossGroup,childGroup)
                    //使用NioSocketChannel 作为服务器的通道实现
                    .channel(NioServerSocketChannel.class)
                    // 设置线程队列得到连接个数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 设置保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 该handler对应 bossGroup
                    //.handler(null)
                    // childHandler 对应 workerGroup
                    .childHandler(new MyServerInitializer());
            ChannelFuture channelFuture = serverBootstrap.bind(7001).sync();
            // 注册监听器
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (channelFuture.isSuccess()) {
                        System.out.println("监听端口 7001 成功");
                    } else {
                        System.out.println("监听端口 7001 失败");
                    }
                }
            });
            // 没有监听到关闭事件时候,主线程不会停止
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }
}
