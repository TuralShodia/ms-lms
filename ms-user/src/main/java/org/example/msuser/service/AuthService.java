package org.example.msuser.service;

import org.example.msuser.entity.User;
import org.example.msuser.enums.Role;
import org.example.msuser.model.request.UserRequest;
import org.example.msuser.model.response.AuthResponse;
import org.example.msuser.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTService jwtService, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthResponse register(UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ADMIN);
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        refreshTokenService.saveRefreshToken(user.getUsername(), refreshToken);

        return new AuthResponse(accessToken, refreshToken);
    }
}
