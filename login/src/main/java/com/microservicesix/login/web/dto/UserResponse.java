package com.microservicesix.login.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class UserResponse {
    private String name;
    private String email;
    private int age;
    private String password;
}
