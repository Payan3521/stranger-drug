package com.microservicesix.login.domain.model;

import java.util.Objects;

public class LoginResponse {
    private String token;
    private User user;

    public LoginResponse(String token, User user){
        this.token = Objects.requireNonNull(token);
        this.user = Objects.requireNonNull(user);
        validateCredentialsResponse();
    }

    private void validateToken(String token){

        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot null or empty");
        }
    }

    private void validateUser(User user){

        if (user == null) {
            throw new IllegalArgumentException("User cannot null");
        }
    }

    private void validateCredentialsResponse(){
        validateToken(token);
        validateUser(user);
    }
}
