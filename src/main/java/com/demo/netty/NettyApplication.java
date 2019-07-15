package com.demo.netty;

import com.demo.netty.im_chat.server.ChatServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyApplication implements CommandLineRunner {

    @Autowired
    private ChatServer chatServer;
    @Value("${netty.port}")
    private int nettyPort;
    @Value("${netty.address}")
    private String nettyAddress;

    public static void main(String[] args) {
        SpringApplication.run(NettyApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        chatServer.start(nettyAddress, nettyPort);
    }
}
