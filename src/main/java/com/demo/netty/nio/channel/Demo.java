package com.demo.netty.nio.channel;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 通过FielChannel将文本输入到文件中去
 */
public class Demo {
    public static void main(String[] args) throws Exception {
        String str = "你好呀！";
        FileOutputStream outputStream = new FileOutputStream("F:/1.txt");
        FileChannel channel = outputStream.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(str.getBytes());
        byteBuffer.flip();
        channel.write(byteBuffer);
        outputStream.close();
    }
}
