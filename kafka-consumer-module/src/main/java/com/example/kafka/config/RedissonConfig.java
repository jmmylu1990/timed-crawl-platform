package com.example.kafka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.redisson.spring.transaction.RedissonTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableTransactionManagement
public class RedissonConfig {


    @Value("${spring.redis.sentinel.nodes}")
    private String redisSentinelNodes;

    @Value("${spring.redis.sentinel.master}")
    private String sentinelMaster;
    @Bean
    @Primary
    public RedissonClient redissonClient() {
        List<String> addSentinelAddresses = Arrays.stream(redisSentinelNodes.split(","))
                .map(host -> "redis://" + host.trim())
                .collect(Collectors.toList());
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec(new ObjectMapper()));
        config.useSentinelServers()
                .setCheckSentinelsList(true)
                .setMasterName(sentinelMaster)
                .setDatabase(0)
                .setKeepAlive(true)
                .setSentinelAddresses(addSentinelAddresses);
        return Redisson.create(config);
    }

    @Bean
    @Primary
    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redissonClient) {
        return new RedissonConnectionFactory(redissonClient);
    }

    @Bean
    public RedissonTransactionManager redissonTransactionManager(RedissonClient redissonClient) {
        return new RedissonTransactionManager(redissonClient);
    }


}
