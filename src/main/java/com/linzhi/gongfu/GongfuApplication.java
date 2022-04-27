package com.linzhi.gongfu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@EnableJpaRepositories
@EnableJpaAuditing
@EnableWebMvc
@EnableWebSocket
@EnableCaching
@SpringBootApplication
public class GongfuApplication {

    public static void main(String[] args) {
        SpringApplication.run(GongfuApplication.class, args);
    }


}
