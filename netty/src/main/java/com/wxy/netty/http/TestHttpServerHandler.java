package com.wxy.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

/*
说明
1. SimpleChannelInboundHandler 是 ChannelInboundHandlerAdapter
2. HttpObject 客户端和服务器端相互通讯的数据被封装成 HttpObject
 */
@Slf4j
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        log.info("对应的channel={}\npipeline={}\n通过pipeline获取channel={}\n当前ctx的handler={}",ctx.channel(),ctx.pipeline(),ctx.pipeline().channel(),ctx.handler());
        //判断 msg 是不是 httprequest请求
        if(msg instanceof HttpRequest) {
            log.info("ctx 类型={}",ctx.getClass());
            log.info("pipeline hashcode={} TestHttpServerHandler hash={}",ctx.pipeline().hashCode(),this.hashCode());
            log.info("msg类型={}",msg.getClass());
            log.info("客户端地址={}",ctx.channel().remoteAddress());
            //获取到
            HttpRequest httpRequest = (HttpRequest) msg;
            //获取uri, 过滤指定的资源
            URI uri = new URI(httpRequest.uri());
            if("/favicon.ico".equals(uri.getPath())) {
                log.info("请求了 favicon.ico, 不做响应");
            }else {
                //回复信息给浏览器 [http协议]
                ByteBuf content = Unpooled.copiedBuffer("hello, 我是服务器", CharsetUtil.UTF_8);
                //构造一个http的相应，即 httpresponse
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
                //将构建好 response返回
                ctx.writeAndFlush(response);
            }
        }
    }
}
