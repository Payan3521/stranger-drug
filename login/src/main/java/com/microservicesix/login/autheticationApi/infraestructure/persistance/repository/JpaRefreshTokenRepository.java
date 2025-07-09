package com.microservicesix.login.autheticationApi.infraestructure.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.microservicesix.login.autheticationApi.infraestructure.persistance.entity.RefreshTokenEntity;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface JpaRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    
    Optional<RefreshTokenEntity> findByToken(String token);
    
    @Modifying
    @Transactional
    @Query("UPDATE RefreshTokenEntity rt SET rt.revoked = true, rt.revokedAt = :revokedAt WHERE rt.userEmail = :userEmail AND rt.revoked = false")
    void revokeAllByUserEmail(@Param("userEmail") String userEmail, @Param("revokedAt") LocalDateTime revokedAt);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshTokenEntity rt WHERE rt.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}