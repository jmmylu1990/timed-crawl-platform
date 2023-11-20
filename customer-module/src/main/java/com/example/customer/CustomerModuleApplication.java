package com.example.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class CustomerModuleApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerModuleApplication.class, args);
    }
}
