package com.demo.netty.im_chat.protocol;

import com.alibaba.fastjson.JSONObject;
import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自定义IM协议的编码器
 */
public class IMEncoder extends MessageToByteEncoder<IMMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, IMMessage msg, ByteBuf out)
            throws Exception {
//		把IMMessage对象序列化为MessagePack
        out.writeBytes(new MessagePack().write(msg));
    }

    public String encode(IMMessage msg) {
        if (null == msg) {
            return "";
        }
        String prex = "[" + msg.getCmd() + "]" + "[" + msg.getTime() + "]";
        if (IMP.LOGIN.getName().equals(msg.getCmd()) ||
                IMP.CHAT.getName().equals(msg.getCmd()) ||
                IMP.FLOWER.getName().equals(msg.getCmd())||
                IMP.IMAGE.getName().equals(msg.getCmd())) {
            prex += ("[" + msg.getSender() + "]");
            prex += ("[" + msg.getAddr() + "]");
            prex += ("[" + msg.getReceiver() + "]");
        } else if (IMP.SYSTEM.getName().equals(msg.getCmd())) {
            prex += ("[" + msg.getOnline() + "]");
        }
        if (msg.getMapList() != null && msg.getMapList().size() > 0) {
            prex += JSONObject.toJSONString(msg.getMapList());
        }
        if (!(null == msg.getContent() || "".equals(msg.getContent()))) {
            prex += (" - " + msg.getContent());
        }
        return prex;
    }

}
