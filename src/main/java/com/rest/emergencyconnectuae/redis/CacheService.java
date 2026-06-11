package com.rest.emergencyconnectuae.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final long DEFAULT_TTL = 300; // 5 minutes

    public void put(String key, Object value) {
        redisTemplate.opsForValue().set(key, value, DEFAULT_TTL, TimeUnit.SECONDS);
    }

    public void put(String key, Object value, long ttlSeconds) {
        redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
    }

    public <T> Optional<T> get(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) return Optional.empty();
        return Optional.of(objectMapper.convertValue(value, type));
    }

    public <T> Optional<T> get(String key, TypeReference<T> typeRef) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) return Optional.empty();
        return Optional.of(objectMapper.convertValue(value, typeRef));
    }

    public void evict(String key) {
        redisTemplate.delete(key);
    }

    public void evictByPattern(String pattern) {
        var keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) redisTemplate.delete(keys);
    }
}