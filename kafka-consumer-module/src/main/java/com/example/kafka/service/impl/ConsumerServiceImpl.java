package com.example.kafka.service.impl;

import com.example.kafka.model.ApiLog;
import com.example.kafka.service.ConsumerService;
import com.example.kafka.service.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ConsumerServiceImpl implements ConsumerService {
    @Value("${kafka.topic.apilog-topic}")
    private String apiLogTopic;
    //無狀態物件
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<ApiLog> apiLogList = new ArrayList<>();
    @Autowired
    private RedisService redisService;


    @KafkaListener(topics = {"${kafka.topic.apilog-topic}"}, groupId = "group3")
    public void consumeMessage3(ConsumerRecord<String, String> consumerRecord) {
        try {
            ApiLog apiLog = objectMapper.readValue(consumerRecord.value(), ApiLog.class);

            synchronized (apiLogList) {
                apiLogList.add(apiLog);
               // log.info("消費者消費topic:{} partition:{}的訊息 -> {}", consumerRecord.topic(), consumerRecord.partition(), apiLog);
            }


        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 10000) // 每隔10秒執行一次
    private void apiLogScheduledTask() {

        if (!apiLogList.isEmpty()) {
            synchronized (apiLogList) {

                redisService.addToList("apiLog", apiLogList);
                int batchSize = apiLogList.size();
                apiLogList.clear();

                log.info("apiLogScheduledTask批次送出:{}筆資料,keyName:{}", batchSize, "apiLog");
            }
        }else{
            log.info("目前無資料");
        }
    }

}
