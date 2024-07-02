package com.kousenit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableDiscoveryClient
public class ClaudeAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClaudeAiApplication.class, args);
    }

}
