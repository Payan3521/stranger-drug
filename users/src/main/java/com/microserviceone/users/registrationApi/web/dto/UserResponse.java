package com.microserviceone.users.registrationApi.web.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class UserResponse {
    private Long id; 
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String rol;
    private LocalDateTime lastLogin;
    private LocalDate birthDate; // Only for customers
    private String area; // Only for admins
    private boolean verifiedCode;
    private boolean verifiedTerm;
}