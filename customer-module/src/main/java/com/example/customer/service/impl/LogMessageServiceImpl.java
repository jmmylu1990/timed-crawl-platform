package com.example.customer.service.impl;

import com.example.customer.conponent.KafkaComponent;
import com.example.customer.service.LogMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Service
public class LogMessageServiceImpl implements LogMessageService {


    @Autowired
    private KafkaComponent kafkaComponent;

    @Transactional("kafkaTransactionManager")
    @Override
    public void sendMessage(String topic, Object message) {
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(topic, null, System.currentTimeMillis(), String.valueOf(message.hashCode()), message);
        kafkaComponent.sendMessage(producerRecord).addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("生產者傳送訊息：{} 失敗，原因：{}", message, ex.getMessage());
                ex.printStackTrace();
            }

            @Override
            public void onSuccess(SendResult<String, Object> sendResult) {
                log.info("生產者成功傳送訊息到" + topic + "-> " + sendResult.getProducerRecord().value().toString());
            }
        });
    }
}
