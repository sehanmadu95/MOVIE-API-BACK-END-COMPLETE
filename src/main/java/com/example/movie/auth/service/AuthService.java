package com.example.movie.auth.service;

import com.example.movie.auth.entity.User;

import com.example.movie.auth.entity.UserRole;
import com.example.movie.auth.repository.UserRepository;
import com.example.movie.auth.utils.AuthResponse;
import com.example.movie.auth.utils.LogingRequest;
import com.example.movie.auth.utils.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse register(RegisterRequest registerRequest){
        var user= User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .userName(registerRequest.getUserName())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole())
                .build();

        User savedUser=   userRepository.save(user);

        var accessToken=jwtService.generateToken(savedUser);
        var refreshToken=refreshTokenService.createRefreshToken(savedUser.getEmail());

        return AuthResponse.builder()
                .accesToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public AuthResponse login(LogingRequest logingRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        logingRequest.getEmail(),
                        logingRequest.getPassword()
                )
        );

        var user=userRepository.findByEmail(logingRequest.getEmail())
                .orElseThrow(()-> new UsernameNotFoundException("User name not found!!"));

        var accessToken=jwtService.generateToken(user);
        var refreshToken=refreshTokenService.createRefreshToken(logingRequest.getEmail());

        return AuthResponse.builder()
                .accesToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }
}
