package com.microserviceone.users.registrationApi.domain.port.out;

public interface ITermsService {
    void validateTermsAndConditions(String email);
}