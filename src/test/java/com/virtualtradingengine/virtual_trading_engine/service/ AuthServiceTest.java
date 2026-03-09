package com.virtualtradingengine.virtual_trading_engine.service;

import com.virtualtradingengine.virtual_trading_engine.dto.AuthResponse;
import com.virtualtradingengine.virtual_trading_engine.dto.LoginRequest;
import com.virtualtradingengine.virtual_trading_engine.entity.Role;
import com.virtualtradingengine.virtual_trading_engine.entity.User;
import com.virtualtradingengine.virtual_trading_engine.repository.UserRepository;
import com.virtualtradingengine.virtual_trading_engine.security.JwtService;
import com.virtualtradingengine.virtual_trading_engine.security.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenService refreshTokenService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                authenticationManager,
                jwtService,
                userRepository,
                passwordEncoder,
                refreshTokenService
        );
    }

    @Test
    void login_shouldReturnAccessAndRefreshTokens() {
        LoginRequest request = new LoginRequest("darshan", "pass123");

        User user = User.builder()
                .id(1L)
                .username("darshan")
                .email("darshan@example.com")
                .password("encoded-password")
                .balance(BigDecimal.valueOf(10000))
                .role(Role.USER)
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new TestingAuthenticationToken(
                        "darshan",
                        null,
                        "ROLE_USER"
                ));

        when(jwtService.generateToken("darshan", "ROLE_USER"))
                .thenReturn("access-token");

        when(userRepository.findByUsername("darshan"))
                .thenReturn(Optional.of(user));

        when(refreshTokenService.issueRefreshToken(user))
                .thenReturn("refresh-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        assertEquals("Bearer", response.tokenType());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken("darshan", "ROLE_USER");
        verify(refreshTokenService, times(1)).issueRefreshToken(user);
    }
}