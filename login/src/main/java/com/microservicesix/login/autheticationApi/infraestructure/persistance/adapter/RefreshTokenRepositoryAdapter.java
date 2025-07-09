package com.microservicesix.login.autheticationApi.infraestructure.persistance.adapter;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Component;

import com.microservicesix.login.autheticationApi.domain.model.RefreshToken;
import com.microservicesix.login.autheticationApi.domain.port.out.IRefreshTokenRepository;
import com.microservicesix.login.autheticationApi.infraestructure.persistance.entity.RefreshTokenEntity;
import com.microservicesix.login.autheticationApi.infraestructure.persistance.mapper.RefreshTokenMapper;
import com.microservicesix.login.autheticationApi.infraestructure.persistance.repository.JpaRefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements IRefreshTokenRepository {

    private final JpaRefreshTokenRepository jpaRepository;
    private final RefreshTokenMapper mapper;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        try {
            
            
            RefreshTokenEntity entity = mapper.toEntity(refreshToken);
            RefreshTokenEntity savedEntity = jpaRepository.save(entity);
            
            
            
            return mapper.toDomain(savedEntity);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        try {
            
            Optional<RefreshTokenEntity> entity = jpaRepository.findByToken(token);
            
            if (entity.isPresent()) {
                return Optional.of(mapper.toDomain(entity.get()));
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void revokeAllByUserEmail(String userEmail) {
        try {
            
            jpaRepository.revokeAllByUserEmail(userEmail, LocalDateTime.now());
            
            
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void deleteExpiredTokens() {
        try {
            
            jpaRepository.deleteExpiredTokens(LocalDateTime.now());
            
            
        } catch (Exception e) {
            throw e;
        }
    }
}
