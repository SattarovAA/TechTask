package ru.effective.tms.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.effective.tms.exception.EntityNotFoundException;
import ru.effective.tms.model.entity.security.AppUserDetails;
import ru.effective.tms.service.security.RefreshTokenService;

import java.security.Key;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Date;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtUtils {
    /**
     * To check Token By User Id.
     *
     * @see #checkTokenByUserId(Long)
     */
    private final RefreshTokenService refreshTokenService;
    /**
     * Jwt Secret Key for generating jwt token.
     *
     * @see #key()
     */
    @Value("${app.security.jwt.secret-key}")
    private String jwtSecret;
    /**
     * Jwt token Expiration.
     * 1 day on default.
     *
     * @see #generateTokenFromUsername(String)
     */
    @Value("${app.security.jwt.expiration}")
    private Duration tokenExpiration;

    /**
     * Generate String Jwt Token from {@link AppUserDetails} userDetails.
     *
     * @param userDetails to get User username.
     * @return string JwtToken.
     */
    public String generateJwtToken(AppUserDetails userDetails) {
        return generateTokenFromUsername(userDetails.getUsername());
    }

    /**
     * Generate Jwt Token from User username.
     * Expiration is now + {@link #tokenExpiration}.
     * Signature Algorithm is HS256.
     *
     * @param username for Subject generate.
     * @return String Jwt Token.
     * @see #generateJwtToken(AppUserDetails)
     */
    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(
                        new Date().getTime() + tokenExpiration.toMillis())
                )
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Key generating by {@link #jwtSecret}.
     * Decoders are BASE64.
     *
     * @return {@link Key} for Jwt Token generating.
     * @see #generateTokenFromUsername(String)
     */
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Parse User username from string jwt token.
     *
     * @param token jwt token to parse.
     * @return User username.
     */
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Jwt token validation.
     *
     * @param authToken token to validate.
     * @return true if token is valid.
     * @throws ExpiredJwtException      if token was expired.
     * @throws UnsupportedJwtException  if token is unsupported.
     * @throws MalformedJwtException    if token is invalid.
     * @throws IllegalArgumentException if claims string is empty.
     */
    public Boolean validation(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Token was expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Check token by user Id.
     *
     * @param userId user to search for token.
     * @throws EntityNotFoundException if token with user id not found!
     */
    public void checkTokenByUserId(Long userId) {
        log.info("refreshTokenService.checkByUserId() with {}", userId);
        if (!refreshTokenService.checkByUserId(userId)) {
            throw new EntityNotFoundException(
                    MessageFormat.format(
                            "RefreshToken with UserId {0} not found!",
                            userId
                    )
            );
        }
    }
}
