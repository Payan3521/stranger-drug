package com.microservicesix.login.autheticationApi.domain.port.in;

public interface IValidateToken {
    boolean validateToken(String token);
}