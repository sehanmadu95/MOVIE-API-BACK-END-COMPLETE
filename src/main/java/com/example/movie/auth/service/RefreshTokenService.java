package com.example.movie.auth.service;

import com.example.movie.auth.entity.RefreshToken;
import com.example.movie.auth.entity.User;
import com.example.movie.auth.repository.RefreshTokenRepository;
import com.example.movie.auth.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(String userName){
        User user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User name Not found!!"));

        RefreshToken refreshToken = user.getRefreshToken();

        if(refreshToken==null){
            long refreshTokenValidy=30*1000;

            refreshToken=RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expireationTime(Instant.now().plusMillis(refreshTokenValidy))
                    .user(user)
                    .build();

            refreshTokenRepository.save(refreshToken);
        }

        return refreshToken;
    }

    public RefreshToken verifyRefreshToken(String refreshToken){
        RefreshToken ref = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()->new RuntimeException("Refresh token not found!!"));

        if (ref.getExpireationTime().compareTo(Instant.now())<0 ){
            refreshTokenRepository.delete(ref);
            throw new RuntimeException("Refresh token expired!!");
        }
        return ref;
    }
}
