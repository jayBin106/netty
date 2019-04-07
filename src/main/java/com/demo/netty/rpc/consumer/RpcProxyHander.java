package com.demo.netty.rpc.consumer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * ·RpcProxyHander
 * ·李文彬
 * 2019/4/7 ·11:17
 */
public class RpcProxyHander extends ChannelInboundHandlerAdapter {
    private Object result;

    public Object getResult() {
        return result;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       this.result = msg;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
