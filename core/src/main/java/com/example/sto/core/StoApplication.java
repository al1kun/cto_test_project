package com.example.sto.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.example.sto.common",
        "com.example.sto.core",
        "com.example.sto.notification"
})
public class StoApplication {
    public static void main(String[] args) {
        SpringApplication.run(StoApplication.class, args);
    }
}