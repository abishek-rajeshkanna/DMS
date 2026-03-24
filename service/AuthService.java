package com.hyundai.DMS.service;

import com.hyundai.DMS.domain.entity.User;
import com.hyundai.DMS.domain.enums.UserStatus;
import com.hyundai.DMS.exception.AccountLockedException;
import com.hyundai.DMS.exception.ForbiddenException;
import com.hyundai.DMS.exception.ResourceNotFoundException;
import com.hyundai.DMS.exception.ValidationException;
import com.hyundai.DMS.repository.UserRepository;
import com.hyundai.DMS.service.TokenService.TokenPair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuditService auditService;

    @Value("${app.auth.max-failed-attempts:5}")
    private int maxFailedAttempts;

    @Value("${app.auth.lock-duration-minutes:30}")
    private int lockDurationMinutes;

    @Transactional
    public TokenPair login(String email, String password, String ipAddress, String userAgent) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid credentials"));

        // Check account status
        if (user.getStatus() == UserStatus.INACTIVE || user.getStatus() == UserStatus.SUSPENDED) {
            auditService.log(user.getId(), dealershipId(user), "LOGIN_FAILED_STATUS",
                    "users", user.getId(), null, null, ipAddress, userAgent);
            throw new ForbiddenException("Account is " + user.getStatus().getValue());
        }

        // Check lock
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            auditService.log(user.getId(), dealershipId(user), "LOGIN_FAILED_LOCKED",
                    "users", user.getId(), null, null, ipAddress, userAgent);
            throw new AccountLockedException("Account is locked until " + user.getLockedUntil());
        }

        // Validate password
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            int attempts = (user.getFailedLoginAttempts() == null ? 0 : user.getFailedLoginAttempts()) + 1;
            user.setFailedLoginAttempts(attempts);
            if (attempts >= maxFailedAttempts) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(lockDurationMinutes));
                log.warn("Account locked for user {} after {} failed attempts", email, attempts);
            }
            userRepository.save(user);
            auditService.log(user.getId(), dealershipId(user), "LOGIN_FAILED_PASSWORD",
                    "users", user.getId(), null, null, ipAddress, userAgent);
            throw new ResourceNotFoundException("Invalid credentials");
        }

        // Success — reset counters
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        auditService.log(user.getId(), dealershipId(user), "LOGIN_SUCCESS",
                "users", user.getId(), null, null, ipAddress, userAgent);

        return tokenService.issueTokenPair(user);
    }

    public TokenPair refresh(String rawRefreshToken) {
        return tokenService.refreshTokens(rawRefreshToken);
    }

    @Transactional
    public void logout(Long userId) {
        tokenService.revokeAllForUser(userId);
        auditService.log(userId, null, "LOGOUT", "users", userId, null, null, null, null);
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword, String ipAddress, String userAgent) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new ValidationException("Current password is incorrect");
        }

        validatePasswordComplexity(newPassword);

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);

        // Revoke all tokens so user must re-login
        tokenService.revokeAllForUser(userId);

        auditService.log(userId, dealershipId(user), "PASSWORD_CHANGED",
                "users", userId, null, null, ipAddress, userAgent);
    }

    private void validatePasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new ValidationException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new ValidationException("Password must contain at least one digit");
        }
        if (!password.matches(".*[^A-Za-z0-9].*")) {
            throw new ValidationException("Password must contain at least one special character");
        }
    }

    private Long dealershipId(User user) {
        return user.getDealership() != null ? user.getDealership().getId() : null;
    }
}
