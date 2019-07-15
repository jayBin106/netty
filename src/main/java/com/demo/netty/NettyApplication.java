package com.demo.netty;

import com.demo.netty.im_chat.server.ChatServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyApplication.class, args);
        try {
            new ChatServer().start(8869);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
