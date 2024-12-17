package ru.effective.tms.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;
import ru.effective.tms.model.entity.security.RefreshToken;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository
        extends CrudRepository<RefreshToken, Long>,
        QueryByExampleExecutor<RefreshToken> {
    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByUserId(Long userId);

    Boolean existsByUserId(Long userId);
}