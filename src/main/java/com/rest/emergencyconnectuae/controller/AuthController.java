package com.rest.emergencyconnectuae.controller;

import com.rest.emergencyconnectuae.impl.AuthServiceImpl;
import com.rest.emergencyconnectuae.models.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthServiceImpl authService;

    public record RegisterRequest(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String email,
            User.Role role
    ) {}

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {}

    public record OtpRequest(
            @NotBlank String username,
            @NotBlank String otp
    ) {}

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        User user = authService.register(req.username(), req.password(), req.email(), req.role());
        return ResponseEntity.ok("User registered: " + user.getUsername());
    }

    @Operation(summary = "Login — returns token or OTP_REQUIRED for dispatchers")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req,
                                   HttpServletRequest request) {
        Object result = authService.login(req.username(), req.password(), request.getRemoteAddr());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Verify OTP for dispatcher MFA — returns JWT token")
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpRequest req,
                                       HttpServletRequest request) {
        String token = authService.verifyMfa(req.username(), req.otp(), request.getRemoteAddr());
        return ResponseEntity.ok(token);
    }
}