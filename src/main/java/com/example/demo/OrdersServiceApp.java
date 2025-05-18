package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class OrdersServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(OrdersServiceApp.class, args);
    }
}
