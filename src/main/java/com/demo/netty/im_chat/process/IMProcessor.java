package com.demo.netty.im_chat.process;

import com.demo.netty.im_chat.protocol.IMDecoder;
import com.demo.netty.im_chat.protocol.IMEncoder;
import com.demo.netty.im_chat.protocol.IMMessage;
import com.demo.netty.im_chat.protocol.IMP;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
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
            onlineUser.add(client);
            //系统通知,循环所有用户如果不是当前用户就提醒
            for (Channel channel : onlineUser) {
                if (client != channel) {
                    message = new IMMessage(IMP.SYSTEM.getName(), System.currentTimeMillis(), onlineUser.size(), senderName);
                } else {
                    message = new IMMessage(IMP.SYSTEM.getName(), System.currentTimeMillis(), onlineUser.size(), "已和服务器建立连接。。");
                }
                //将消息对象发给客户端
                String encodeText = encoder.encode(message);
                channel.writeAndFlush(new TextWebSocketFrame(encodeText));
            }

        } else if (IMP.LOGOUT.getName().equals(message.getCmd())) {
            onlineUser.remove(client);
        }
    }

    public void logout(Channel client) {
        onlineUser.remove(client);
    }
}
