package com.demo.netty.im_chat.server.handler;

import com.demo.netty.im_chat.process.IMProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * WebSocketHandler
 * <p>
 * liwenbin
 * 2019/4/5 15:23
 *
 *
 * 实现SimpleChannelInboundHandler接口，泛型是TextWebSocketFrame
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private IMProcessor imProcessor = new IMProcessor();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("接收消息：" + msg.text());
        imProcessor.process(ctx.channel(), msg.text());
    }

    //监听浏览器退出，关闭窗口
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("有人退出了。。");
        imProcessor.logout(ctx.channel());
    }
}
