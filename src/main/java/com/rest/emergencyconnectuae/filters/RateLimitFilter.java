package com.rest.emergencyconnectuae.filters;

import com.rest.emergencyconnectuae.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtil jwtUtil;

    @Value("${app.rate-limit.requests-per-minute}")
    private int defaultLimit;

    @Value("${app.rate-limit.dispatcher-requests-per-minute}")
    private int dispatcherLimit;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        String role = extractRole(request);
        int limit = "DISPATCHER".equals(role) ? dispatcherLimit : defaultLimit;

        String key = "rate:" + ip;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, 1, TimeUnit.MINUTES);
        }

        if (count != null && count > limit) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Too many requests. Slow down.\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    private String extractRole(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                return jwtUtil.extractRole(header.substring(7));
            } catch (Exception ignored) {}
        }
        return "ANONYMOUS";
    }
}