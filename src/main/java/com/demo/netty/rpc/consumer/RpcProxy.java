package com.demo.netty.rpc.consumer;

import com.demo.netty.rpc.msg.InvokerMsg;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * ·RpcProxy
 * ·李文彬
 * 2019/4/7 ·10:55
 */
public class RpcProxy {
    public static <T> T create(Class<?> clazz) {
        MethProxy methProxy = new MethProxy(clazz);
//        伪代理对象
        T t = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, methProxy);
        return t;
    }


}


//伪代理
class MethProxy implements InvocationHandler {
    private Class<?> clazz;

    public MethProxy(Class<?> clazz) {
        this.clazz = clazz;
    }

    //代理，调用接口中每个方法的时候，实际上就是发起一次网络请求
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //如果传进来的是一个已经实现的具体的类，直接忽略
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        //如果进来的是一个接口，就走远程调用
        else {
            return rpcInvoke(method, args);
        }
    }

    private Object rpcInvoke(Method method, Object[] args) {
        //封装msg
        InvokerMsg msg = new InvokerMsg();
        msg.setClassName(clazz.getName());
        msg.setMethodName(method.getName());
        msg.setParames(method.getParameterTypes());
        msg.setValues(args);

        final RpcProxyHander hander = new RpcProxyHander();


        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        try {
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sh) throws Exception {
//                            开启串行任务链
//                   处理的 拆包，黏包，的解码编码器
                            sh.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            sh.pipeline().addLast(new LengthFieldPrepender(4));

                            //协议用Java，jdk默认的序列化
                            sh.pipeline().addLast("encoder", new ObjectEncoder());
//                   解码 不让做缓存
                            sh.pipeline().addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

                            //自定义业务
                            sh.pipeline().addLast(hander);

                        }
                    });
            ChannelFuture future = b.connect("localhost", 82).sync();
            System.out.println("客户端远程调用rpcInvoke....");
            future.channel().writeAndFlush(msg).sync();
            future.channel().closeFuture().sync();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

        return hander.getResult();
    }
}
