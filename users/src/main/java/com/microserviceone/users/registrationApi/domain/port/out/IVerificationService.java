package com.microserviceone.users.registrationApi.domain.port.out;

public interface IVerificationService {
    void validateEmailVerification(String email);
}