package com.microservicesix.login.autheticationApi.infraestructure.external.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String rol;
    private LocalDate birthDate; // Only for customers
    private String area; // Only for admins
    private boolean verifiedCode;
    private boolean verifiedTerm;
}