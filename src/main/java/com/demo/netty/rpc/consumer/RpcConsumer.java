package com.demo.netty.rpc.consumer;

import com.demo.netty.rpc.api.IRpcHello;

/**
 * ·RpcConsumer
 * ·李文彬
 * 2019/4/7 ·9:23
 * 服务消费者
 */

public class RpcConsumer {

    public static void main(String[] args) {
        //使用动态代理实现远程调用

        //用动态代理实现，接口传进去，获取一个实例对象，伪代理
        IRpcHello aClass = RpcProxy.create(IRpcHello.class);
        String jay = aClass.say("jay");
        System.out.println(jay);

    }
}
