package com.demo.netty.rpc.provider;

import com.demo.netty.rpc.api.IRpcHello;

/**
 * ·RpcHello
 * ·李文彬
 * 2019/4/7 ·9:25
 *
 * 服务提供者
 */
public class RpcHello implements IRpcHello {
    @Override
    public String say(String name) {
        return name + "吃饭了么？";
    }
}
