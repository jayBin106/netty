package com.demo.netty.nio.channel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 把一个文件内容取出来，到另一个文件中去
 */
public class Demo3 {
    public static void main(String[] args) throws Exception {
        File file1 = new File("F:/1.txt");
        File file2 = new File("F:/2.txt");
        FileChannel in = new FileInputStream(file1).getChannel();
        FileChannel out = new FileOutputStream(file2).getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        while (true) {
            /**
             *          position = 0;
             *         limit = capacity;
             *         mark = -1;
             *         return this;
             */
            byteBuffer.clear(); //读完之后要清空,不然position还是10 ，就会读到0
            int read = in.read(byteBuffer);
            if (read == -1) {//标识读完了
                break;
            } else {
                byteBuffer.flip();
                out.write(byteBuffer);
            }
        }
        in.close();
        out.close();
    }
}
