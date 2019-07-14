package com.demo.netty.im_chat.fastDFS;


import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import java.net.InetSocketAddress;

public class FastDFSClient {

    private TrackerClient trackerClient = null;
    private TrackerServer trackerServer = null;
    private StorageServer storageServer = null;
    private StorageClient1 storageClient = null;

    public FastDFSClient(String fastDFSPath) throws Exception {
        //初始化加载文件的方式
//        if (conf.contains("classpath:")) {
//            conf = conf.replace("classpath:", this.getClass().getResource("/").getPath());
//        }
//        ClientGlobal.init(conf);
        String[] parts = fastDFSPath.split("\\:", 2);
        if (parts.length != 2) {
            throw new MyException("the value of item \"tracker_server\" is invalid, the correct format is host:port");
        }
        //配置文件的方式
        InetSocketAddress[] tracker_servers = new InetSocketAddress[1];
        tracker_servers[0] = new InetSocketAddress(parts[0].trim(), Integer.parseInt(parts[1].trim()));
        TrackerGroup tracker_group = new TrackerGroup(tracker_servers);
        trackerClient = new TrackerClient(tracker_group);
        trackerServer = trackerClient.getConnection();
        storageServer = null;
        storageClient = new StorageClient1(trackerServer, storageServer);
    }

    /**
     * 上传文件方法
     * <p>Title: uploadFile</p>
     * <p>Description: </p>
     *
     * @param fileName 文件全路径
     * @param extName  文件扩展名，不包含（.）
     * @param metas    文件扩展信息
     * @return
     * @throws Exception
     */
    public String uploadFile(String fileName, String extName, NameValuePair[] metas) throws Exception {
        String result = storageClient.upload_file1(fileName, extName, metas);
        return result;
    }

    public String uploadFile(String fileName) throws Exception {
        return uploadFile(fileName, null, null);
    }

    public String uploadFile(String fileName, String extName) throws Exception {
        return uploadFile(fileName, extName, null);
    }

    /**
     * 上传文件方法
     * <p>Title: uploadFile</p>
     * <p>Description: </p>
     *
     * @param fileContent 文件的内容，字节数组
     * @param extName     文件扩展名
     * @param metas       文件扩展信息
     * @return
     * @throws Exception
     */
    public String uploadFile(byte[] fileContent, String extName, NameValuePair[] metas) throws Exception {

        String result = storageClient.upload_file1(fileContent, extName, metas);
        return result;
    }

    public String uploadFile(byte[] fileContent) throws Exception {
        return uploadFile(fileContent, null, null);
    }

    public String uploadFile(byte[] fileContent, String extName) throws Exception {
        return uploadFile(fileContent, extName, null);
    }
}
