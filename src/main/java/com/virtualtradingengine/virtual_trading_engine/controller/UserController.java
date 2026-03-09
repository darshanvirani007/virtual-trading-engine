package com.virtualtradingengine.virtual_trading_engine.controller;

import com.virtualtradingengine.virtual_trading_engine.dto.UserResponse;
import com.virtualtradingengine.virtual_trading_engine.entity.User;
import com.virtualtradingengine.virtual_trading_engine.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get current logged-in user profile")
    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal UserDetails principal) {

        User user = userService.getByUsernameOrThrow(principal.getUsername());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getBalance()
        );
    }
}