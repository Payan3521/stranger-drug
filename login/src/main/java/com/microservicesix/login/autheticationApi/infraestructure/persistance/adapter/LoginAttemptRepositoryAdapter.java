package com.microservicesix.login.autheticationApi.infraestructure.persistance.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.microservicesix.login.autheticationApi.domain.model.LoginAttempt;
import com.microservicesix.login.autheticationApi.domain.port.out.ILoginAttemptRepository;
import com.microservicesix.login.autheticationApi.infraestructure.persistance.entity.LoginAttemptEntity;
import com.microservicesix.login.autheticationApi.infraestructure.persistance.mapper.LoginAttemptMapper;
import com.microservicesix.login.autheticationApi.infraestructure.persistance.repository.JpaLoginAttemptRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginAttemptRepositoryAdapter implements ILoginAttemptRepository {

    private final JpaLoginAttemptRepository jpaRepository;
    private final LoginAttemptMapper mapper;

    @Override
    public LoginAttempt save(LoginAttempt loginAttempt) {
        try {
            
            LoginAttemptEntity entity = mapper.toEntity(loginAttempt);
            LoginAttemptEntity savedEntity = jpaRepository.save(entity);
            
           
            return mapper.toDomain(savedEntity);
        } catch (Exception e) {
            
            throw e;
        }
    }

    @Override
    public List<LoginAttempt> findFailedAttemptsByEmailSince(String email, LocalDateTime since) {
        try {
            
            List<LoginAttemptEntity> entities = jpaRepository.findFailedAttemptsByEmailSince(email, since);
            
            
            
            return entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<LoginAttempt> findFailedAttemptsByIpSince(String ipAddress, LocalDateTime since) {
        try {
            
            List<LoginAttemptEntity> entities = jpaRepository.findFailedAttemptsByIpSince(ipAddress, since);
            
            
            
            return entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public long countFailedAttemptsByEmailSince(String email, LocalDateTime since) {
        try {
            
            long count = jpaRepository.countFailedAttemptsByEmailSince(email, since);
            
            
            
            return count;
        } catch (Exception e) {
            
            throw e;
        }
    }
}