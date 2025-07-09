package com.microservicesix.login.autheticationApi.domain.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttempt {
    private Long id;
    private String email;
    private String ipAddress;
    private boolean successful;
    private String failureReason;
    private LocalDateTime attemptTime;
    private String userAgent;

    public static LoginAttempt successful(String email, String ipAddress, String userAgent) {
        return LoginAttempt.builder()
                .email(email)
                .ipAddress(ipAddress)
                .successful(true)
                .attemptTime(LocalDateTime.now())
                .userAgent(userAgent)
                .build();
    }

    public static LoginAttempt failed(String email, String ipAddress, String userAgent, String reason) {
        return LoginAttempt.builder()
                .email(email)
                .ipAddress(ipAddress)
                .successful(false)
                .failureReason(reason)
                .attemptTime(LocalDateTime.now())
                .userAgent(userAgent)
                .build();
    }
}
