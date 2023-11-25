package com.example.customer.conponent;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;

@Component
public class KafkaComponent {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Async
    public ListenableFuture<SendResult<String, Object>> sendMessage(ProducerRecord<String, Object> producerRecord) {
        return kafkaTemplate.send(producerRecord);
    }
}
