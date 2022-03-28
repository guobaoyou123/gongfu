package com.linzhi.gongfu.infrastructure;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedOrigins(
                    "http://localhost:9090",
                    "http://localhost:3000",
                    "http://124.71.134.200:3000",
                    "http://192.168.2.111:3000/")
                .allowedMethods("OPTIONS","PUT","DELETE","GET","POST","PATCH");
    }
}
