package com.microservicesix.login.autheticationApi.infraestructure.persistance.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.microservicesix.login.autheticationApi.infraestructure.persistance.entity.LoginAttemptEntity;

@Repository
public interface JpaLoginAttemptRepository extends JpaRepository<LoginAttemptEntity, Long> {
    
    @Query("SELECT la FROM LoginAttemptEntity la WHERE la.email = :email AND la.successful = false AND la.attemptTime >= :since ORDER BY la.attemptTime DESC")
    List<LoginAttemptEntity> findFailedAttemptsByEmailSince(@Param("email") String email, @Param("since") LocalDateTime since);
    
    @Query("SELECT la FROM LoginAttemptEntity la WHERE la.ipAddress = :ipAddress AND la.successful = false AND la.attemptTime >= :since ORDER BY la.attemptTime DESC")
    List<LoginAttemptEntity> findFailedAttemptsByIpSince(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(la) FROM LoginAttemptEntity la WHERE la.email = :email AND la.successful = false AND la.attemptTime >= :since")
    long countFailedAttemptsByEmailSince(@Param("email") String email, @Param("since") LocalDateTime since);
}