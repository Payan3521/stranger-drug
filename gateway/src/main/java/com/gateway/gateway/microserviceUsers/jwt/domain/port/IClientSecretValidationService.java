package com.gateway.gateway.microserviceUsers.jwt.domain.port;

public interface IClientSecretValidationService {
    boolean isValidClientSecret(String clientSecret);
    String getExpectedClientSecret();
}