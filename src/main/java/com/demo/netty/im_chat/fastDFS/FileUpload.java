package com.demo.netty.im_chat.fastDFS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * FileUpload
 * <p>
 * liwenbin
 * 2019/7/15 10:45
 */
public class FileUpload {
    /**
     *
     * @param file_orignal 被上传的文件路径
     * @param file_des      上传后的文件路径
     */
    public static void copyFile(String file_orignal, String file_des) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel fileInChannel = null;
        FileChannel fileOutChannel = null;
        try {
            fis = new FileInputStream(file_orignal);
            //输出流
            File file = new File(file_des);
            if (!file.exists() && !file.isDirectory()){
                file.mkdirs();
            }
            File destFile = new File(file_des);
            fos = new FileOutputStream(destFile);
            fileInChannel = fis.getChannel();
            fileOutChannel = fos.getChannel();
            fileInChannel.transferTo(0, fileInChannel.size(), fileOutChannel);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fileInChannel != null) {
                    fileInChannel.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (fileOutChannel != null) {
                    fileOutChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
