package com.demo.netty.BioDemo;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BioServer {
    public static void main(String[] args) throws IOException {
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();

        ServerSocket serverSocket = new ServerSocket(9999);

        System.out.println("服务启动啦");
        while (true) {
            System.out.println("id = " + Thread.currentThread().getId() + " name = " + Thread.currentThread().getName());
            System.out.println("服务监听中...........");
            final Socket socket = serverSocket.accept();
            newCachedThreadPool.execute(new job(socket));
        }
    }
}

class job implements Runnable {
    private Socket socket;

    public job(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {

            System.out.println("id = " + Thread.currentThread().getId() + " name = " + Thread.currentThread().getName());
            byte[] bytes = new byte[1024];
            InputStream inputStream = socket.getInputStream();
            while (true) {

                System.out.println("等待读取客户端发送数据...........");
                int read = inputStream.read(bytes);
                if (read != -1) {
//                    输出客户端数据
                    System.out.print(new String(bytes, 0, read));
                } else {
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                System.out.println("read...........");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
