package com.demo.netty.tomcat.server;

import com.demo.netty.tomcat.http.GPRequest;
import com.demo.netty.tomcat.http.GPResponse;
import com.demo.netty.tomcat.sevlets.MyServlet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;

/**
 * GPTomcatHandler
 * <p>
 * liwenbin
 * 2019/4/5 9:40
 */
public class GPTomcatHandler extends ChannelInboundHandlerAdapter {
    public GPTomcatHandler() {
    }

    /**
     * 执行read方法
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            GPRequest gpRequest = new GPRequest(ctx, request);
            GPResponse gpResponse = new GPResponse(ctx, request);
            new MyServlet().doGet(gpRequest, gpResponse);
            System.out.println("channelRead====================== ");
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        System.out.println("channelReadComplete===================");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println("exceptionCaught=======================");
    }
}
