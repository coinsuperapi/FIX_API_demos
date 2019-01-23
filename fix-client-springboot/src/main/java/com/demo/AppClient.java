package com.demo;

import io.allune.quickfixj.spring.boot.starter.EnableQuickFixJClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableQuickFixJClient
@SpringBootApplication
public class AppClient {

    public static void main(String[] args) {
        SpringApplication.run(AppClient.class, args);
    }
}
