package com.demo.netty.tomcat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;

/**
 * GPTomcat
 * <p>
 * liwenbin
 * 2019/4/5 9:23
 * <p>
 * netty实现tomcat的原理，数据请求到tomcat都是字符串，有servlet只是一个方法和抽象类，
 * 真正调用方法的是自己写的那个类，request和response都是通过服务器返回的，
 */
public class GPTomcat {
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
                            //编码器
                            client.pipeline().addLast(new HttpResponseEncoder());
                            //解码器
                            client.pipeline().addLast(new HttpRequestDecoder());
                            //业务逻辑处理
                            client.pipeline().addLast(new GPTomcatHandler());
                        }
                    })
                    //配置信息
                    //主线程的配置，设置主进程不终止   128表示分配线程的最大数量
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //子线程配置
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            //启动服务  。sync是同步操作会有一个阻塞的过程，线程会处理等待中
            ChannelFuture future = serverBootstrap.bind(port).sync();
            System.out.println("GPTomcat已经启动");
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
            new GPTomcat().start(8080);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

        }

    }
}
