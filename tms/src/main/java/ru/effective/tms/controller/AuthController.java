package ru.effective.tms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.effective.tms.mapper.UserMapper;
import ru.effective.tms.model.dto.SimpleResponse;
import ru.effective.tms.model.dto.security.AuthResponse;
import ru.effective.tms.model.dto.security.LoginRequest;
import ru.effective.tms.model.dto.security.RefreshTokenRequest;
import ru.effective.tms.model.dto.security.RefreshTokenResponse;
import ru.effective.tms.model.dto.user.UserRequest;
import ru.effective.tms.service.security.SecurityService;

@Tag(name = "AuthenticationController",
        description = "User authentication controller.")
@RequiredArgsConstructor
@RequestMapping("api/auth")
@RestController
public class AuthController {
    private final SecurityService securityService;
    private final UserMapper userMapper;

    @Operation(
            summary = "Authentication user.",
            tags = {"auth", "post", "public"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = AuthResponse.class))
            }),
            @ApiResponse(responseCode = "400"),
    })
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authUser(
            @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(securityService.authenticationUser(loginRequest));
    }

    @Operation(
            summary = "Register new User.",
            tags = {"auth", "post", "register", "public"})
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {
                    @Content(schema = @Schema(implementation = SimpleResponse.class))
            }),
            @ApiResponse(responseCode = "400"),
    })
    @PostMapping("/register")
    public ResponseEntity<SimpleResponse> registerUser(
            @RequestBody @Valid UserRequest userRequest) {
        securityService.registerNewUser(userMapper.requestToModel(userRequest));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SimpleResponse("User created!"));
    }

    @Operation(
            summary = "Refresh accessToken by refreshToken.",
            tags = {"auth", "post"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = RefreshTokenResponse.class))
            }),
            @ApiResponse(responseCode = "403"),
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(securityService.refreshToken(request));
    }

    @Operation(
            summary = "Logout user.",
            tags = {"auth", "post", "logout"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = SimpleResponse.class))
            }),
            @ApiResponse(responseCode = "403"),
    })
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SimpleResponse> logoutUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        securityService.logout();
        SimpleResponse simpleResponse = new SimpleResponse(
                "User logout. Username is: " + userDetails.getUsername()
        );
        return ResponseEntity.status(HttpStatus.OK)
                .body(simpleResponse);
    }
}
