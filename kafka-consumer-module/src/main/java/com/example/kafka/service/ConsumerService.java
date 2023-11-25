package com.example.kafka.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerService {
    void consumeMessage3(ConsumerRecord<String, String> consumerRecord);
}
