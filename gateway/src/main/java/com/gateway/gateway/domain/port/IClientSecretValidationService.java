package com.gateway.gateway.domain.port;

public interface IClientSecretValidationService {
    boolean isValidClientSecret(String clientSecret);
    String getExpectedClientSecret();
}