package com.example.customer.service.impl;

import com.example.customer.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Component
public class RedisServiceImpl implements RedisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisServiceImpl.class);
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public void setNormalCache(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            LOGGER.error("exception when set key {}. ", key, e);
        }
    }

    @Override
    public void setCacheWithTime(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                this.setNormalCache(key, value);
            }
        } catch (Exception e) {
            LOGGER.error("exception when set key {}. ", key, e);
        }
    }

    @Override
    public void setExpireTime(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            LOGGER.error("exception when expire key {}. ", key, e);
        }
    }

    @Override
    public long getExpireTime(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

  @Override
    public boolean hasKeyInCache(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            LOGGER.error("exception when check key {}. ", key, e);
            return false;
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public void deleteChche(String... key) {
        if (Objects.nonNull(key) && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        }
    }

    @Override
    public Object getNormalCache(String key) {
        return Objects.isNull(key) ? null : redisTemplate.opsForValue().get(key);
    }

    public long increment(String key, long delta) {
        if (delta <= 0) {
            throw new RuntimeException("遞增因子必須大於0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public long decrement(String key, long delta) {
        if (delta <= 0) {
            throw new RuntimeException("遞減因子必須大於0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

  @Override
    public Properties getRedisInfo(String type) {
        return Objects.isNull(type) ? stringRedisTemplate.getRequiredConnectionFactory().getConnection().info()
                : stringRedisTemplate.getRequiredConnectionFactory().getConnection().info(type);
    }

}
