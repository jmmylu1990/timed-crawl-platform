package com.example.kafka.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

public interface KafkaService {
//    public ListenableFuture<SendResult<String, Object>> sendMessage(String topic, Object message);

    ListenableFuture<SendResult<String, Object>> sendMessage(ProducerRecord<String, Object> producerRecord);


}
