package com.microservicesix.login.autheticationApi.infraestructure.persistance.mapper;

import org.springframework.stereotype.Component;

import com.microservicesix.login.autheticationApi.domain.model.RefreshToken;
import com.microservicesix.login.autheticationApi.infraestructure.persistance.entity.RefreshTokenEntity;

@Component
public class RefreshTokenMapper {

    public RefreshTokenEntity toEntity(RefreshToken domain) {
        if (domain == null) {
            return null;
        }

        return RefreshTokenEntity.builder()
                .id(domain.getId())
                .token(domain.getToken())
                .userEmail(domain.getUserEmail())
                .expiryDate(domain.getExpiryDate())
                .revoked(domain.isRevoked())
                .createdAt(domain.getCreatedAt())
                .revokedAt(domain.getRevokedAt())
                .build();
    }

    public RefreshToken toDomain(RefreshTokenEntity entity) {
        if (entity == null) {
            return null;
        }

        return RefreshToken.builder()
                .id(entity.getId())
                .token(entity.getToken())
                .userEmail(entity.getUserEmail())
                .expiryDate(entity.getExpiryDate())
                .revoked(entity.isRevoked())
                .createdAt(entity.getCreatedAt())
                .revokedAt(entity.getRevokedAt())
                .build();
    }
}