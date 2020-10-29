package com.demo.netty.nio.channel;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 把文件内容输入到控制台
 */
public class Demo2 {
    public static void main(String[] args)  throws Exception{
        FileInputStream fileInputStream = new FileInputStream("F:/1.txt");
        FileChannel channel = fileInputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        channel.read(byteBuffer);
        byteBuffer.flip();
        System.out.println(new String(byteBuffer.array()));
        fileInputStream.close();
    }
}
