package com.demo.netty.im_chat.process;

import com.alibaba.fastjson.JSONObject;
import com.demo.netty.im_chat.protocol.IMDecoder;
import com.demo.netty.im_chat.protocol.IMEncoder;
import com.demo.netty.im_chat.protocol.IMMessage;
import com.demo.netty.im_chat.protocol.IMP;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * IMProcessor
 * <p>
 * liwenbin
 * 2019/4/5 16:02
 */
public class IMProcessor {
    private final static ChannelGroup onlineUser = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //编码器 字符串转对象
    private IMDecoder decoder = new IMDecoder();
    //解码器
    private IMEncoder encoder = new IMEncoder();

    //属性参数
    private final AttributeKey<String> NICK_NAME = AttributeKey.valueOf("nickName");
    private final AttributeKey<String> IP_ADDR = AttributeKey.valueOf("ipAddr");
    private final AttributeKey<JSONObject> ATTRS = AttributeKey.valueOf("attrs");


    /**
     * 获取用户昵称
     *
     * @param client
     * @return
     */
    public String getNickName(Channel client) {
        return client.attr(NICK_NAME).get();
    }

    /**
     * 获取用户远程IP地址
     *
     * @param client
     * @return
     */
    public String getAddress(Channel client) {
        return client.remoteAddress().toString().replaceFirst("/", "");
    }

    /**
     * 获取扩展属性
     *
     * @param client
     * @return
     */
    public JSONObject getAttrs(Channel client) {
        try {
            return client.attr(ATTRS).get();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取扩展属性
     *
     * @param client
     * @return
     */
    private void setAttrs(Channel client, String key, Object value) {
        try {
            JSONObject json = client.attr(ATTRS).get();
            json.put(key, value);
            client.attr(ATTRS).set(json);
        } catch (Exception e) {
            JSONObject json = new JSONObject();
            json.put(key, value);
            client.attr(ATTRS).set(json);
        }
    }

    public void process(Channel client, String msg) {
        //将消息编码后才能对象
        IMMessage message = decoder.decode(msg);
        if (message == null) {
            return;
        }
        //获取发送人消息
        String senderName = message.getSender();

        //判断如果是登录动作，就onlineUser加一
        if (message.getCmd().equals(IMP.LOGIN.getName())) {
            //把登录人名称放入变量中
            client.attr(NICK_NAME).getAndSet(message.getSender());
            onlineUser.add(client);
            //系统通知,循环所有用户如果不是当前用户就提醒
            for (Channel channel : onlineUser) {
                if (client != channel) {
                    message = new IMMessage(IMP.SYSTEM.getName(), System.currentTimeMillis(), onlineUser.size(), senderName + "进入聊天室");
                } else {
                    message = new IMMessage(IMP.SYSTEM.getName(), System.currentTimeMillis(), onlineUser.size(), "已和服务器建立连接。。");
                }
                //将消息对象发给客户端
                send(channel, message);
            }

        } else if (IMP.LOGOUT.getName().equals(message.getCmd())) {
            message = new IMMessage(IMP.SYSTEM.getName(), System.currentTimeMillis(), onlineUser.size(), senderName + "进入聊天室");
            //将消息对象发给客户端
            send(client, message);
            //人数减一
            onlineUser.remove(client);
        } else if (IMP.CHAT.getName().equals(message.getCmd())) {
            for (Channel channel : onlineUser) {
                if (channel != client) {
                    message.setSender(getNickName(client));
                } else {
                    message.setSender("我");
                }
                send(channel, message);
            }
        } else if (IMP.FLOWER.getName().equals(message.getCmd())) {
            //客户端发送鲜花命令
            JSONObject attrs = getAttrs(client);
            //正常送花
            for (Channel channel : onlineUser) {
                if (channel == client) {
                    message.setSender("我");
                    message.setContent("你给大家送了一波鲜花雨");
                    setAttrs(client, "lastFlowerTime", System.currentTimeMillis());
                } else {
                    message.setSender(getNickName(client));
                    message.setContent(getNickName(client) + "给大家送了一波鲜花雨");
                }
                message.setTime(System.currentTimeMillis());
                send(channel, message);
            }


        }
    }

    /**
     * //将消息对象发给客户端
     * 消息发送
     *
     * @param channel
     * @param message
     */
    private void send(Channel channel, IMMessage message) {
        String encodeText = encoder.encode(message);
        channel.writeAndFlush(new TextWebSocketFrame(encodeText));
    }

    public void logout(Channel client) {
        onlineUser.remove(client);
    }
}
