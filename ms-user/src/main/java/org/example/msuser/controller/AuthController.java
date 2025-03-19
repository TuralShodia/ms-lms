package org.example.msuser.controller;

import lombok.RequiredArgsConstructor;
import org.example.msuser.entity.User;
import org.example.msuser.model.request.UserRequest;
import org.example.msuser.model.response.AuthResponse;
import org.example.msuser.model.request.LoginRequest;
import org.example.msuser.model.request.LogoutRequest;
import org.example.msuser.model.request.RefreshTokenRequest;
import org.example.msuser.service.AuthService;
import org.example.msuser.service.JWTService;
import org.example.msuser.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        UserDetails user = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenService.saveRefreshToken(user.getUsername(), refreshToken);

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();
        String username = jwtService.extractUsername(refreshToken);

        if (username != null && refreshTokenService.validateRefreshToken(username, refreshToken)) {
            UserDetails user = userDetailsService.loadUserByUsername(username);
            String newAccessToken = jwtService.generateAccessToken(user);
            return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));
        }
        throw new RuntimeException("Invalid refresh token");
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) {
        refreshTokenService.deleteRefreshToken(request.username());
        return ResponseEntity.ok().build();
    }
}
