package com.microservicesix.login.autheticationApi.infraestructure.persistance.mapper;

import org.springframework.stereotype.Component;
import com.microservicesix.login.autheticationApi.domain.model.LoginAttempt;
import com.microservicesix.login.autheticationApi.infraestructure.persistance.entity.LoginAttemptEntity;

@Component
public class LoginAttemptMapper {

    public LoginAttemptEntity toEntity(LoginAttempt domain) {
        if (domain == null) {
            return null;
        }

        return LoginAttemptEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .ipAddress(domain.getIpAddress())
                .successful(domain.isSuccessful())
                .failureReason(domain.getFailureReason())
                .attemptTime(domain.getAttemptTime())
                .userAgent(domain.getUserAgent())
                .build();
    }

    public LoginAttempt toDomain(LoginAttemptEntity entity) {
        if (entity == null) {
            return null;
        }

        return LoginAttempt.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .ipAddress(entity.getIpAddress())
                .successful(entity.isSuccessful())
                .failureReason(entity.getFailureReason())
                .attemptTime(entity.getAttemptTime())
                .userAgent(entity.getUserAgent())
                .build();
    }
}
