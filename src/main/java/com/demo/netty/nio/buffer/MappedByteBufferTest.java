package com.demo.netty.nio.buffer;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 */
public class MappedByteBufferTest {
    public static void main(String[] args) throws Exception {
        RandomAccessFile accessFile = new RandomAccessFile("1.txt", "rw");
        FileChannel channel = accessFile.getChannel();
        /**
         * 参数1：FileChannel.MapMode.READ_WRITE 使用的读写模式
         * 参数2：0 直接修改的起始位置
         * 参数3： 5 是映射到内存的大小（不是索引位置），即将1。txt的多少个字节映射到内存可以直接修改的范围就是0-5
         * 实际类型是DirectByteBuffer
         */
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);
        mappedByteBuffer.put(0, (byte) '9');
        mappedByteBuffer.put(4, (byte) 'H');
        channel.close();
        System.out.println("修改成功！ ");
    }
}
