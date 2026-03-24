package com.hyundai.DMS.controller;

import com.hyundai.DMS.dto.request.ChangePasswordRequest;
import com.hyundai.DMS.dto.request.LoginRequest;
import com.hyundai.DMS.dto.request.RefreshRequest;
import com.hyundai.DMS.dto.response.AuthResponse;
import com.hyundai.DMS.security.DmsUserDetails;
import com.hyundai.DMS.service.AuthService;
import com.hyundai.DMS.service.TokenService.TokenPair;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${app.jwt.access-token-expiry:900}")
    private long accessTokenExpiry;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletRequest httpRequest) {
        TokenPair pair = authService.login(
                request.getEmail(),
                request.getPassword(),
                httpRequest.getRemoteAddr(),
                httpRequest.getHeader("User-Agent")
        );
        return ResponseEntity.ok(toAuthResponse(pair));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        TokenPair pair = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(toAuthResponse(pair));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal DmsUserDetails principal) {
        authService.logout(principal.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                                @AuthenticationPrincipal DmsUserDetails principal,
                                                HttpServletRequest httpRequest) {
        authService.changePassword(
                principal.getUserId(),
                request.getCurrentPassword(),
                request.getNewPassword(),
                httpRequest.getRemoteAddr(),
                httpRequest.getHeader("User-Agent")
        );
        return ResponseEntity.noContent().build();
    }

    private AuthResponse toAuthResponse(TokenPair pair) {
        return AuthResponse.builder()
                .accessToken(pair.accessToken())
                .refreshToken(pair.refreshToken())
                .expiresIn(accessTokenExpiry)
                .tokenType("Bearer")
                .build();
    }
}
