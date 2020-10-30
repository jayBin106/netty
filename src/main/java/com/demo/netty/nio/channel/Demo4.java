package com.demo.netty.nio.channel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * transferFrom() 直接进行channel拷贝
 */
public class Demo4 {
    public static void main(String[] args) throws Exception{
        File file1 = new File("F:/1.txt");
        File file2 = new File("F:/3.txt");
        FileChannel in = new FileInputStream(file1).getChannel();
        FileChannel out = new FileOutputStream(file2).getChannel();
        out.transferFrom(in,0,in.size());
        in.close();
        out.close();
    }

}
