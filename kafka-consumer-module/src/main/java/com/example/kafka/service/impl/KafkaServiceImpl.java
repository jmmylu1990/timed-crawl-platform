package com.example.kafka.service.impl;

import com.example.kafka.service.KafkaService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
public class KafkaServiceImpl implements KafkaService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Async
    @Override
    public ListenableFuture<SendResult<String, Object>> sendMessage(ProducerRecord<String, Object> producerRecord) {
         return kafkaTemplate.send(producerRecord);
    }
}
