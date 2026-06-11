package com.rest.emergencyconnectuae.impl;

import com.rest.emergencyconnectuae.models.User;
import com.rest.emergencyconnectuae.redis.OtpService;
import com.rest.emergencyconnectuae.redis.SessionService;
import com.rest.emergencyconnectuae.repo.UserRepository;
import com.rest.emergencyconnectuae.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final SessionService sessionService;
    private final AuditService auditService;

    public User register(String username, String rawPassword, String email, User.Role role) {
        if (userRepository.existsByUsername(username))
            throw new RuntimeException("Username already taken.");
        if (userRepository.existsByEmail(email))
            throw new RuntimeException("Email already registered.");
        return userRepository.save(new User(username, passwordEncoder.encode(rawPassword), email, role));
    }

    // Step 1 for DISPATCHER: validate credentials, send OTP
    // Step 1 for others: validate credentials, return token immediately
    public Object login(String username, String rawPassword, String ip) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials."));

        if (!passwordEncoder.matches(rawPassword, user.getPassword()))
            throw new RuntimeException("Invalid credentials.");

        if (!user.isActive())
            throw new RuntimeException("Account is disabled.");

        auditService.log(username, "LOGIN_ATTEMPT", "Step 1", ip);

        if (user.getRole() == User.Role.DISPATCHER) {
            String otp = otpService.generateAndStoreOtp(username);
            // In production: send via email/SMS. For demo: log it.
            System.out.println("[OTP] " + username + " => " + otp);
            return "OTP_REQUIRED";
        }

        return issueToken(user, ip);
    }

    // Step 2 for DISPATCHER: verify OTP, issue token
    public String verifyMfa(String username, String otp, String ip) {
        if (!otpService.verifyOtp(username, otp))
            throw new RuntimeException("Invalid or expired OTP.");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found."));

        return issueToken(user, ip);
    }

    private String issueToken(User user, String ip) {
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        String sessionId = UUID.randomUUID().toString();
        sessionService.createSession(sessionId, user.getUsername(), user.getRole().name());
        auditService.log(user.getUsername(), "LOGIN_SUCCESS", "Session created", ip);
        return token;
    }
}