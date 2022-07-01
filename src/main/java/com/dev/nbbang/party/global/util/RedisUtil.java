package com.dev.nbbang.party.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate redisTemplate;

    public String getList(String key) {
        RedisOperations<String, String> operations = redisTemplate.opsForList().getOperations();
        return operations.opsForList().leftPop(key);
    }

    public void setList(String key, String value) {
        RedisOperations<String, String> operations = redisTemplate.opsForList().getOperations();
        operations.opsForList().leftPush(key, value);
    }

    public long deleteList(String key, String value) {
        RedisOperations<String, String> operations = redisTemplate.opsForList().getOperations();
        return operations.opsForList().remove(key, 0, value);
    }

    public long getListSize(String key) {
        RedisOperations<String, String> operations = redisTemplate.opsForList().getOperations();
        return operations.opsForList().size(key);
    }

    public String getData(String key) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        return vop.get(key);
    }

    public void setData(String key, String value) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        vop.set(key, value);
    }

    public Boolean deleteData(String key) {
        return redisTemplate.delete(key);
    }
}
