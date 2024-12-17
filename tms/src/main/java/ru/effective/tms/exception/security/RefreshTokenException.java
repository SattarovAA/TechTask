package ru.effective.tms.exception.security;

import java.text.MessageFormat;

public class RefreshTokenException extends RuntimeException {
    public RefreshTokenException(String token, String message) {
        super(MessageFormat.format("error trying to refresh token {0}:{1}",
                token,
                message)
        );
    }

    public RefreshTokenException(String message) {
        super(message);
    }
}
