package com.rest.emergencyconnectuae.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final long OTP_TTL = 300; // 5 minutes

    public String generateAndStoreOtp(String username) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        redisTemplate.opsForValue().set("otp:" + username, otp, OTP_TTL, TimeUnit.SECONDS);
        return otp;
    }

    public boolean verifyOtp(String username, String inputOtp) {
        Object stored = redisTemplate.opsForValue().get("otp:" + username);
        if (stored == null) return false;
        boolean valid = stored.toString().equals(inputOtp);
        if (valid) redisTemplate.delete("otp:" + username); // single use
        return valid;
    }

    public boolean hasPendingOtp(String username) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("otp:" + username));
    }
}