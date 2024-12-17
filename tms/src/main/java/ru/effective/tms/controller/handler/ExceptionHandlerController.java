package ru.effective.tms.controller.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.effective.tms.exception.AccessException;
import ru.effective.tms.exception.AlreadyExitsException;
import ru.effective.tms.exception.EntityNotFoundException;
import ru.effective.tms.exception.InnerException;
import ru.effective.tms.exception.security.RefreshTokenException;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {
    /**
     * ExceptionHandler for {@link DuplicateKeyException}.
     * <br>
     * Used for aggregate validation exceptions.
     *
     * @param ex exception type of {@link DuplicateKeyException}.
     * @return {@link ResponseEntity} with {@link ErrorResponse}.
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorResponse> notFound(
            DuplicateKeyException ex) {
        log.info("duplicate key: " + ex.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getLocalizedMessage()));
    }

    /**
     * ExceptionHandler for {@link HttpMessageNotReadableException}.
     * <br>
     * Used for aggregate validation exceptions.
     *
     * @param ex exception type of {@link HttpMessageNotReadableException}.
     * @return {@link ResponseEntity} with {@link ErrorResponse}.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> notFound(
            HttpMessageNotReadableException ex) {
        log.info("BAD_REQUEST key: " + ex.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getLocalizedMessage()));
    }

    /**
     * ExceptionHandler for {@link AccessException}.
     * <br>
     * Used for aggregate validation exceptions.
     *
     * @param ex exception type of {@link AccessException}.
     * @return {@link ResponseEntity} with {@link ErrorResponse}.
     */
    @ExceptionHandler(AccessException.class)
    public ResponseEntity<ErrorResponse> notFound(AccessException ex) {
        log.info("User doesn't have access right: "
                + ex.getLocalizedMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getLocalizedMessage()));
    }

    /**
     * ExceptionHandler for {@link InnerException}.
     * <br>
     * Used for aggregate validation exceptions.
     *
     * @param ex exception type of {@link InnerException}.
     * @return {@link ResponseEntity} with {@link ErrorResponse}.
     */
    @ExceptionHandler(InnerException.class)
    public ResponseEntity<ErrorResponse> notFound(InnerException ex) {
        log.info("Inner exception: " + ex.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ex.getLocalizedMessage()));
    }

    /**
     * ExceptionHandler for {@link MethodArgumentNotValidException}.
     * <br>
     * Used for aggregate validation exceptions.
     *
     * @param ex exception type of {@link MethodArgumentNotValidException}.
     * @return {@link ResponseEntity} with {@link ErrorResponse}.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> notFound(
            MethodArgumentNotValidException ex) {
        log.info("not valid argument: " + ex.getLocalizedMessage());
        BindingResult bindingResult = ex.getBindingResult();
        List<String> errorMessages = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        String errorMessage = String.join("; ", errorMessages);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(errorMessage));
    }

    /**
     * ExceptionHandler for {@link RefreshTokenException}.
     * <br>
     * Used for aggregate validation exceptions.
     *
     * @param ex exception type of {@link RefreshTokenException}.
     * @return {@link ResponseEntity} with {@link ErrorResponse}.
     */
    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<ErrorResponseBody> refreshTokenExceptionHandler(
            RefreshTokenException ex, WebRequest webRequest) {
        return buildResponse(HttpStatus.FORBIDDEN, ex, webRequest);
    }

    /**
     * ExceptionHandler for {@link AlreadyExitsException}.
     * <br>
     * Used for aggregate validation exceptions.
     *
     * @param ex exception type of {@link AlreadyExitsException}.
     * @return {@link ResponseEntity} with {@link ErrorResponse}.
     */
    @ExceptionHandler(AlreadyExitsException.class)
    public ResponseEntity<ErrorResponseBody> alreadyExitsHandler(
            AlreadyExitsException ex, WebRequest webRequest) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex, webRequest);
    }

    /**
     * ExceptionHandler for {@link EntityNotFoundException}.
     * <br>
     * Used for aggregate validation exceptions.
     *
     * @param ex exception type of {@link EntityNotFoundException}.
     * @return {@link ResponseEntity} with {@link ErrorResponse}.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseBody> entityNotFoundHandler(
            EntityNotFoundException ex, WebRequest webRequest) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex, webRequest);
    }

    /**
     * ExceptionHandler for {@link AccessDeniedException}.
     * <br>
     * Used for aggregate validation exceptions.
     *
     * @param ex exception type of {@link AccessDeniedException}.
     * @return {@link ResponseEntity} with {@link ErrorResponse}.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseBody> forbidden(
            AccessDeniedException ex, WebRequest webRequest) {
        return buildResponse(HttpStatus.FORBIDDEN, ex, webRequest);
    }

    /**
     * Exception response builder.
     *
     * @param status     exception {@link HttpStatus}.
     * @param ex         exception.
     * @param webRequest web request for exception description.
     * @return {@link ResponseEntity} with {@link ErrorResponseBody}.
     */
    private ResponseEntity<ErrorResponseBody> buildResponse(
            HttpStatus status, Exception ex, WebRequest webRequest) {
        return ResponseEntity.status(status)
                .body(ErrorResponseBody.builder()
                        .message(ex.getMessage())
                        .description(webRequest.getDescription(false))
                        .build());
    }
}
