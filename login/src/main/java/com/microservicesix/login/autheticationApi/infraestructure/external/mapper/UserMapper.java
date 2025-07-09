package com.microservicesix.login.autheticationApi.infraestructure.external.mapper;

import org.springframework.stereotype.Component;
import com.microservicesix.login.autheticationApi.domain.model.User;
import com.microservicesix.login.autheticationApi.infraestructure.external.dto.UserResponse;

@Component
public class UserMapper {

    public User toDomain(UserResponse dto) {
        if (dto == null) {
            return null;
        }

        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .phone(dto.getPhone())
                .role(mapRole(dto.getRol()))
                .verifiedCode(dto.isVerifiedCode())
                .verifiedTerm(dto.isVerifiedTerm())
                .active(true) // Asumimos que si está en la base de datos, está activo
                .build();
    }

    private User.UserRole mapRole(String role) {
        if (role == null) {
            return User.UserRole.CUSTOMER;
        }
        
        try {
            return User.UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return User.UserRole.CUSTOMER;
        }
    }
}
