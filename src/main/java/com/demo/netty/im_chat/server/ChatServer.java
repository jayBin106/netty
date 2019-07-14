package com.demo.netty.im_chat.server;

import com.demo.netty.im_chat.protocol.IMDecoder;
import com.demo.netty.im_chat.protocol.IMEncoder;
import com.demo.netty.im_chat.server.handler.HttpHandler;
import com.demo.netty.im_chat.server.handler.SocketHandler;
import com.demo.netty.im_chat.server.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * GPTomcatServer
 * <p>
 * liwenbin
 * 2019/4/5 9:23
 * <p>
 * netty实现tomcat的原理，数据请求到tomcat都是字符串，有servlet只是一个方法和抽象类，
 * 真正调用方法的是自己写的那个类，request和response都是通过服务器返回的，
 */
public class ChatServer {
    public void start(int port) throws Exception {
        //传统nio
//        ServerSocketChannel open = ServerSocketChannel.open();
//        open.bind(local);

        //传统的bio
//        ServerSocket serverSocket = new ServerSocket(port);

        //netty方式
        //主从方式
        // boss线程
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        //worker线程
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        //netty服务
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(bossGroup, workerGroup)  //收主线程和子线程
                    .channel(NioServerSocketChannel.class) //主线程处理类
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //客户端处理
                        @Override
                        protected void initChannel(SocketChannel client) throws Exception {
                            //无锁化 串行编程
                            //添加自定义socker协议
                            client.pipeline().addLast(new IMDecoder());
                            client.pipeline().addLast(new IMEncoder());
                            client.pipeline().addLast(new SocketHandler());
                            //用来解码和编码http请求
                            client.pipeline().addLast(new HttpServerCodec());
                            //为了实现继承SimpleChannelInboundHandler而加的，
                            // 参数是能够接收到最大的 , 请求头大小
                            client.pipeline().addLast(new HttpObjectAggregator(64 * 1024));
                            //用于处理文件流的hander
                            //自定义的拦截器
                            //用来处理文件流的一个hander,主要用来处理大文件
                            client.pipeline().addLast(new ChunkedWriteHandler());
                            client.pipeline().addLast(new HttpHandler());


                            //=========================接受webSocker请求====================================
                            //开启websocker  im前以ws开头，包含im的认为是走websocket协议
                            client.pipeline().addLast(new WebSocketServerProtocolHandler("/im"));
                            client.pipeline().addLast(new WebSocketHandler());


                        }
                    })
                    //配置信息
                    //主线程的配置，设置主进程不终止   1024表示分配线程的最大数量
                    .option(ChannelOption.SO_BACKLOG, 1024);

            //启动服务  。sync是同步操作会有一个阻塞的过程，线程会处理等待中
            ChannelFuture future = serverBootstrap.bind(port).sync();
            System.out.println("ChatServer已经启动81");
            future.channel().closeFuture().sync();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            //关闭服务
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        try {
            new ChatServer().start(81);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

        }

    }
}
