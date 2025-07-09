package com.microservicesix.login.autheticationApi.domain.port.out;

import java.time.LocalDateTime;
import java.util.List;

import com.microservicesix.login.autheticationApi.domain.model.LoginAttempt;

public interface ILoginAttemptRepository {
    LoginAttempt save(LoginAttempt loginAttempt);
    List<LoginAttempt> findFailedAttemptsByEmailSince(String email, LocalDateTime since);
    List<LoginAttempt> findFailedAttemptsByIpSince(String ipAddress, LocalDateTime since);
    long countFailedAttemptsByEmailSince(String email, LocalDateTime since);
}