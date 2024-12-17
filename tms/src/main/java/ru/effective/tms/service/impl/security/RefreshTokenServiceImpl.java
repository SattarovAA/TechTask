package ru.effective.tms.service.impl.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.effective.tms.model.entity.security.RefreshToken;
import ru.effective.tms.repository.RefreshTokenRepository;
import ru.effective.tms.exception.security.RefreshTokenException;
import ru.effective.tms.service.security.RefreshTokenService;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${app.security.jwt.refresh-token.expiration}")
    private Duration refreshTokenExpiry;

    public Optional<RefreshToken> findByRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiry.toMillis()))
                .token(UUID.randomUUID().toString())
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken checkRefreshToken(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException(token.getToken(),
                    "Refresh token was expired. Repeat signin action!");
        }
        return token;
    }

    public void deleteByUserId(Long userId) {
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUserId(userId);
        refreshTokenRepository.deleteAll(tokens);
    }

    @Override
    public boolean checkByUserId(Long userId) {
        return refreshTokenRepository.existsByUserId(userId);
    }

    public void setRefreshTokenExpiry(Duration refreshTokenExpiry) {
        this.refreshTokenExpiry = refreshTokenExpiry;
    }
}
