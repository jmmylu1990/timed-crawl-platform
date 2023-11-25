package com.example.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topic.my-topic}")
    String myTopic;
    @Value("${kafka.topic.my-topic2}")
    String myTopic2;

    @Value("${kafka.topic.apilog-topic}")
    private String apiLogTopic;

    /**
     * JSON訊息轉換器
     */
    @Bean
    public RecordMessageConverter jsonConverter() {
        return new StringJsonMessageConverter();
    }

    /**
     * 透過注入一個 NewTopic 類型的 Bean 來建立 topic，如果 topic 已存在，則會忽略。
     */
    @Bean
    public NewTopic myTopic() {
        return new NewTopic(myTopic, 2, (short) 1);
    }

    @Bean
    public NewTopic myTopic2() {
        return new NewTopic(myTopic2, 1, (short) 1);
    }

    @Bean
    public NewTopic apiLogTopic() {
        return new NewTopic(apiLogTopic, 2, (short) 1);
    }
}
