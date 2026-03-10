package com.wc;

import com.wc.rocket.Sink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.tio.websocket.starter.EnableTioWebSocketServer;
import org.tio.websocket.starter.TioWebSocketServerBootstrap;


@SpringBootApplication
@EnableTioWebSocketServer // 开启 tio 的 websocket
@EnableBinding(Sink.class) // 启用 Stream 绑定
public class ChanServiceApplication {

    @Autowired
    private TioWebSocketServerBootstrap bootstrap ;

    public static void main(String[] args) {
        SpringApplication.run(ChanServiceApplication.class ,args) ;
    }

}
