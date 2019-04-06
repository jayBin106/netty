package com.demo.netty.im_chat.server.handler;

import com.demo.netty.im_chat.process.IMProcessor;
import com.demo.netty.im_chat.protocol.IMMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * ·SocketHandler
 * ·李文彬
 * 2019/4/6 ·16:22
 */
public class SocketHandler extends SimpleChannelInboundHandler<IMMessage> {

    private IMProcessor processor = new IMProcessor();

    @Override
    protected void channelRead0(ChannelHandlerContext context, IMMessage message) throws Exception {
        processor.process(context.channel(), message);
    }
}
