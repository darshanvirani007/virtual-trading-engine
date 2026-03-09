package com.virtualtradingengine.virtual_trading_engine.service;

import com.virtualtradingengine.virtual_trading_engine.dto.*;
import com.virtualtradingengine.virtual_trading_engine.entity.Role;
import com.virtualtradingengine.virtual_trading_engine.entity.User;
import com.virtualtradingengine.virtual_trading_engine.exception.ConflictException;
import com.virtualtradingengine.virtual_trading_engine.exception.NotFoundException;
import com.virtualtradingengine.virtual_trading_engine.repository.UserRepository;
import com.virtualtradingengine.virtual_trading_engine.security.JwtService;
import com.virtualtradingengine.virtual_trading_engine.security.RefreshTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    public UserResponse register(AuthRegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username already exists: " + request.username());
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .balance(request.startingBalance())
                .role(Role.USER)
                .build();

        User saved = userRepository.save(user);

        log.info("User registered: username={}, id={}", saved.getUsername(), saved.getId());

        return new UserResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getBalance()
        );
    }

    public AuthResponse login(LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()
        );

        var authentication = authenticationManager.authenticate(authToken);

        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("ROLE_USER");

        String accessToken = jwtService.generateToken(request.username(), role);

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new NotFoundException("User not found: " + request.username()));

        String refreshToken = refreshTokenService.issueRefreshToken(user);

        log.info("User login: username={}, role={}", user.getUsername(), role);

        return new AuthResponse(accessToken, refreshToken, "Bearer");
    }

    public RefreshResponse refresh(RefreshRequest request) {

        String username = refreshTokenService.validateAndRevoke(request.refreshToken());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        Role roleEnum = (user.getRole() != null) ? user.getRole() : Role.USER;
        String role = "ROLE_" + roleEnum.name();

        String accessToken = jwtService.generateToken(user.getUsername(), role);
        String newRefreshToken = refreshTokenService.issueRefreshToken(user);

        log.info("Token refreshed (rotated): username={}", user.getUsername());

        return new RefreshResponse(accessToken, newRefreshToken, "Bearer");
    }

    public void logout(LogoutRequest request) {
        refreshTokenService.revoke(request.refreshToken());
        log.info("User logout: refresh token revoked");
    }
}