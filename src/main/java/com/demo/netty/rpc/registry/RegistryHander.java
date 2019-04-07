package com.demo.netty.rpc.registry;

import com.demo.netty.im_chat.protocol.IMMessage;
import com.demo.netty.rpc.msg.InvokerMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ·RegistryHander
 * ·李文彬
 * 2019/4/7 ·9:47
 * <p>
 * <p>
 * 处理整个注册中心的业务逻辑
 * <p>
 * 约定只要是在provider包下的类，都认为是一个可以对外提供服务的实现类 com.demo.netty.rpc.provider
 * <p>
 * 扫描包下的类，注册到注册中心中
 * <p>
 * 服务端通过客户端传的InvokerMsg的值 ，来确定具体哪个类来执行。
 */
public class RegistryHander extends ChannelInboundHandlerAdapter {
    //在注册中心的服务需要一个容器存储
    public static ConcurrentHashMap map = new ConcurrentHashMap();
    private List<String> classCache = new ArrayList<String>();

    public RegistryHander() {
        scanClass("com.demo.netty.rpc.provider");
        doRegister();
    }

    /**
     * 来接受客户端传过来的值
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Object result = new Object();
        //客户段传过来的调用信息
        InvokerMsg message = (InvokerMsg) msg;
        //判断缓存中是否存在该key
        if (map.containsKey(message.getClassName())) {
            Object obj = map.get(message.getClassName());
            Method method = obj.getClass().getMethod(message.getMethodName(), message.getParames());
            result = method.invoke(obj, message.getValues());
        }
        ctx.writeAndFlush(result);
        ctx.close();
    }

    /**
     * 异常捕获
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }

    //扫描所有的class
    private void scanClass(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        File file = new File(url.getFile());
        for (File f : file.listFiles()) {
//            判断是否是个文件夹
            if (f.isDirectory()) {
                scanClass(packageName + "." + f.getName());
            } else {
//                获取类路径
                String calssName = packageName + "." + f.getName().replace(".class", "").trim();
                classCache.add(calssName);
            }
        }

    }

    //把扫描的所有class实例化，放入到map中
    //注册的服务名字就是接口的名字
//    约定优于配置
    private void doRegister() {
        if (classCache != null && classCache.size() > 0) {
            for (String className : classCache) {
                try {
                    Class<?> clazz = Class.forName(className);
                    //服务名字
                    Class<?> interf = clazz.getInterfaces()[0];
                    map.put(interf.getName(), clazz.newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
