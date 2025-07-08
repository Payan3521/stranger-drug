package com.microservicesix.login.domain.model;

import java.util.Objects;

import com.microservicesix.login.domain.exception.InvalidCredentialsException;


public class LoginRequest {

    private String email;
    private String password;

    public LoginRequest(String email, String password){
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.password = Objects.requireNonNull(password, "Password cannot be null");
        validateCredentials();
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    private void validateEmail(String email){
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidCredentialsException("Email cannot be null or empty");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void validatePassword(String password){
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidCredentialsException("Password cannot null or empty");
        }
        if (password.length() < 8 ) {
            throw new InvalidCredentialsException("Password must be at least 8 characters long");
        }
    }

    private void validateCredentials(){
        validateEmail(email);
        validatePassword(password);
    }
}
