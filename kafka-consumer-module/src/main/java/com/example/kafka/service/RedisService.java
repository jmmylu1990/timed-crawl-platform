package com.example.kafka.service;

import com.example.kafka.model.ApiLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

public interface RedisService {



     void addToList(String logName, List<?> apiLogs);
     List<Object> getAllFromList(String logName);



//     void addToHash(String hashName, Map<String, Object> objectMap);
//     List<Object> getAllFromHash(String hashName);
}
