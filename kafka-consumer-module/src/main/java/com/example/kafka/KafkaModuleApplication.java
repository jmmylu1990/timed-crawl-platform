package com.example.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KafkaModuleApplication {
    public static void main(String[] args) {
        SpringApplication.run(KafkaModuleApplication.class, args);
    }
}
