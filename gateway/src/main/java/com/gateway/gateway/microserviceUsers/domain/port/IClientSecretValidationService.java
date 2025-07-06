package com.gateway.gateway.microserviceUsers.domain.port;

public interface IClientSecretValidationService {
    boolean isValidClientSecret(String clientSecret);
    String getExpectedClientSecret();
}