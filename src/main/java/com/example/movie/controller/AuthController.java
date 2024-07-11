package com.example.movie.controller;

import com.example.movie.auth.entity.RefreshToken;
import com.example.movie.auth.entity.User;
import com.example.movie.auth.service.AuthService;

import com.example.movie.auth.service.JwtService;
import com.example.movie.auth.service.RefreshTokenService;
import com.example.movie.auth.utils.AuthResponse;
import com.example.movie.auth.utils.LogingRequest;
import com.example.movie.auth.utils.RefreshTokenRequest;
import com.example.movie.auth.utils.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;



    @PostMapping("/register")
    public ResponseEntity<AuthResponse> resgiterUser(@RequestBody RegisterRequest registerRequest){
      return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LogingRequest logingRequest){
        return ResponseEntity.ok(authService.login(logingRequest));
    }


    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse>getNewAccessTokenUsingRefreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        RefreshToken refreshToken=refreshTokenService.verifyRefreshToken(
                refreshTokenRequest.getRefreshToken()
        );

        User user=refreshToken.getUser();

        String accessToken=jwtService.generateToken(user);

        return ResponseEntity.ok(AuthResponse.builder()
                .accesToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build());
    }

}
