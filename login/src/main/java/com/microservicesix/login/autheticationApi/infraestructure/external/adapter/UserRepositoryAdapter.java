package com.microservicesix.login.autheticationApi.infraestructure.external.adapter;

import java.util.Optional;
import org.springframework.stereotype.Component;
import com.microservicesix.login.autheticationApi.domain.model.User;
import com.microservicesix.login.autheticationApi.domain.port.out.IUserRepository;
import com.microservicesix.login.autheticationApi.infraestructure.external.UserServiceClient;
import com.microservicesix.login.autheticationApi.infraestructure.external.dto.ApiResponse;
import com.microservicesix.login.autheticationApi.infraestructure.external.dto.UserResponse;
import com.microservicesix.login.autheticationApi.infraestructure.external.mapper.UserMapper;
import com.microservicesix.login.core.config.internalSecurity.InternalJwtService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements IUserRepository {

    private final UserServiceClient userServiceClient;
    private final UserMapper userMapper;
    private final InternalJwtService internalJwtService;

    @Override
    public Optional<User> findByEmail(String email) {
        try {
           
            
            String token = internalJwtService.generateToken("login-service");
            String authHeader = "Bearer " + token;
            
            ApiResponse<UserResponse> response = userServiceClient.findUserByEmail(email, authHeader);
            
            if (response.isSuccess() && response.getData() != null) {
              
                User user = userMapper.toDomain(response.getData());
                return Optional.of(user);
            } else {
               
                return Optional.empty();
            }
        } catch (Exception e) {
           
            return Optional.empty();
        }
    }

    @Override
    public void updateLastLogin(String email) {
        // Por ahora no implementamos la actualización del último login
        // Se puede agregar un endpoint en el microservicio de usuarios si es necesario
       
    }
}
