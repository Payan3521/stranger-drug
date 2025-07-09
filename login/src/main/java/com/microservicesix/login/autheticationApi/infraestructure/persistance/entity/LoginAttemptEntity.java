package com.microservicesix.login.autheticationApi.infraestructure.persistance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_attempts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttemptEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "email", nullable = false)
    private String email;
    
    @Column(name = "ip_address", nullable = false)
    private String ipAddress;
    
    @Column(name = "successful", nullable = false)
    private boolean successful;
    
    @Column(name = "failure_reason")
    private String failureReason;
    
    @Column(name = "attempt_time", nullable = false)
    private LocalDateTime attemptTime;
    
    @Column(name = "user_agent")
    private String userAgent;
}