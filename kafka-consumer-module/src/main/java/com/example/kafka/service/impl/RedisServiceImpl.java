package com.example.kafka.service.impl;

import com.example.kafka.model.ApiLog;
import com.example.kafka.service.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    //no status object
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Transactional
    @Override
    public void addToList(String logName,List<?> apiLogs) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        try {

            List<String> jsonValues = apiLogs.stream()
                    .map(apiLog -> {
                        try {
                            return objectMapper.writeValueAsString(apiLog);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("Error converting ApiLog to JSON", e);
                        }
                    })
                    .toList();

            listOperations.rightPushAll(logName, jsonValues);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Transactional
    @Override
    public List<Object> getAllFromList(String logName) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        return listOperations.range(logName, 0, -1);
    }
}
