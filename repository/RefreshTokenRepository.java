package com.hyundai.DMS.repository;

import com.hyundai.DMS.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findAllByUserIdAndIsRevokedFalse(Long userId);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.isRevoked = true, r.revokedAt = CURRENT_TIMESTAMP WHERE r.user.id = :userId AND r.isRevoked = false")
    int revokeAllByUserId(@Param("userId") Long userId);
}
