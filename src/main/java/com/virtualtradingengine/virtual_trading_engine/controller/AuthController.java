package com.virtualtradingengine.virtual_trading_engine.controller;

import com.virtualtradingengine.virtual_trading_engine.dto.*;
import com.virtualtradingengine.virtual_trading_engine.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a new user (creates account)")
    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody AuthRegisterRequest request) {
        return authService.register(request);
    }

    @Operation(summary = "Login and receive JWT access token + refresh token")
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(summary = "Exchange refresh token for a new access token")
    @PostMapping("/refresh")
    public RefreshResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request);
    }

    @Operation(summary = "Logout (revoke refresh token)")
    @PostMapping("/logout")
    public void logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
    }
}