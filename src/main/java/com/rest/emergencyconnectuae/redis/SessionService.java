package com.rest.emergencyconnectuae.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final long SESSION_TTL = 1800; // 30 minutes

    public void createSession(String sessionId, String username, String role) {
        String key = "session:" + sessionId;
        redisTemplate.opsForHash().put(key, "username", username);
        redisTemplate.opsForHash().put(key, "role", role);
        redisTemplate.expire(key, SESSION_TTL, TimeUnit.SECONDS);
    }

    public String getSessionUser(String sessionId) {
        Object val = redisTemplate.opsForHash().get("session:" + sessionId, "username");
        return val != null ? val.toString() : null;
    }

    public String getSessionRole(String sessionId) {
        Object val = redisTemplate.opsForHash().get("session:" + sessionId, "role");
        return val != null ? val.toString() : null;
    }

    public void invalidateSession(String sessionId) {
        redisTemplate.delete("session:" + sessionId);
    }

    public boolean sessionExists(String sessionId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("session:" + sessionId));
    }
}