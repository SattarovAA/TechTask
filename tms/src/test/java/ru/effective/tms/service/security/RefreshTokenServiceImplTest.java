package ru.effective.tms.service.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.effective.tms.exception.security.RefreshTokenException;
import ru.effective.tms.model.entity.security.RefreshToken;
import ru.effective.tms.repository.RefreshTokenRepository;
import ru.effective.tms.service.impl.security.RefreshTokenServiceImpl;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@DisplayName("RefreshTokenServiceImplTest Tests")
public class RefreshTokenServiceImplTest {
    private RefreshTokenServiceImpl refreshTokenService;
    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;
    private final Duration refreshTokenExpiry = Duration.ofMinutes(30);

    @BeforeEach
    void setUp() {
        refreshTokenService =
                new RefreshTokenServiceImpl(refreshTokenRepository);
        refreshTokenService.setRefreshTokenExpiry(refreshTokenExpiry);
    }

    @Test
    @DisplayName("findByRefreshToken test: get optional Refresh Token " +
            "from String token.")
    void givenExistingStringTokenWhenFindByRefreshTokenThenRefreshToken() {
        String token = "token";
        Optional<RefreshToken> optionalRefreshToken = Optional.of(
                new RefreshToken(1L, 1L, "token", Instant.now())
        );

        Mockito.when(refreshTokenRepository.findByToken(token))
                .thenReturn(optionalRefreshToken);

        Optional<RefreshToken> actual =
                refreshTokenService.findByRefreshToken(token);

        assertEquals(optionalRefreshToken, actual);
        Mockito.verify(refreshTokenRepository, Mockito.times(1))
                .findByToken(any());
    }

    @Test
    @DisplayName("createRefreshToken test: get Refresh Token from userId.")
    void givenUserIdWhenCreateRefreshTokenThenRefreshToken() {
        Long userId = 1L;
        RefreshToken refreshToken = new RefreshToken(
                null, 1L, "token", Instant.now()
        );

        Mockito.when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenReturn(refreshToken);
        Long actual =
                refreshTokenService.createRefreshToken(userId).getUserId();

        assertEquals(userId, actual);
        Mockito.verify(refreshTokenRepository, Mockito.times(1))
                .save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("checkRefreshToken test: check RefreshToken expiry.")
    void givenCorrectRefreshTokenWhenCheckRefreshTokenThenRefreshToken() {
        RefreshToken refreshToken = new RefreshToken(
                1L, 1L, "token", Instant.now()
        );

        RefreshToken actual =
                refreshTokenService.checkRefreshToken(refreshToken);

        assertEquals(refreshToken, actual);
        Mockito.verify(refreshTokenRepository, Mockito.times(0))
                .delete(refreshToken);
    }

    @Test
    @DisplayName("checkRefreshToken: check incorrect RefreshToken expiry.")
    void givenIncorrectRefreshTokenWhenCheckRefreshTokenThenRefreshToken() {
        RefreshToken refreshToken = new RefreshToken(
                1L, 1L, "token", Instant.MIN
        );

        assertThrows(RefreshTokenException.class,
                () -> refreshTokenService.checkRefreshToken(refreshToken),
                " refreshToken is incorrect."
        );
        Mockito.verify(refreshTokenRepository, Mockito.times(1))
                .delete(refreshToken);
    }
}
