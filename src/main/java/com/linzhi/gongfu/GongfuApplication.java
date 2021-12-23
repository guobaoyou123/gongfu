package com.linzhi.gongfu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@EnableJpaRepositories
@EnableWebMvc
@EnableWebSocket
@SpringBootApplication
public class GongfuApplication {

    public static void main(String[] args) {
        SpringApplication.run(GongfuApplication.class, args);
    }

}
