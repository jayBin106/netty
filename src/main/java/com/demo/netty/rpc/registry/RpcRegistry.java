package com.demo.netty.rpc.registry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * ·RpcRegistry
 * ·李文彬
 * 2019/4/7 ·9:23
 */
//注册中心
public class RpcRegistry {

    int port;

    public RpcRegistry(int port) {
        this.port = port;
    }

    public void strart() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel sh) throws Exception {
//                    开启串行任务链
//                   处理的 拆包，黏包，的解码编码器
                    sh.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                    sh.pipeline().addLast(new LengthFieldPrepender(4));

                    //协议用Java，jdk默认的序列化
                    sh.pipeline().addLast("encoder", new ObjectEncoder());
//                   解码 不让做缓存
                    sh.pipeline().addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

                    //自定义业务
                    sh.pipeline().addLast(new RegistryHander());

                }
            })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
//           启动服务
            ChannelFuture future = b.bind(this.port).sync();
            System.out.println("rpc服务启动" + this.port);
            future.channel().closeFuture().sync();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }


    public static void main(String[] args) {
        new RpcRegistry(82).strart();
    }
}
