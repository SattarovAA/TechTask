package ru.effective.tms.model.entity.security;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Instant;

/**
 * RefreshToken entity for working with Redis.
 */
@RedisHash("refresh_tokens")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RefreshToken {
    @Id
    @Indexed
    private Long id;
    @Indexed
    private Long userId;
    @Indexed
    private String token;
    @Indexed
    private Instant expiryDate;
}