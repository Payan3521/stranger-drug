package com.microservicesix.login.autheticationApi.domain.port.out;

import java.util.Optional;
import com.microservicesix.login.autheticationApi.domain.model.User;

public interface IUserRepository {
    Optional<User> findByEmail(String email);
    void updateLastLogin(String email);
}