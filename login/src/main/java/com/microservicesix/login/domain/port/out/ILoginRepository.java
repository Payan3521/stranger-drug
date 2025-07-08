package com.microservicesix.login.domain.port.out;

import java.util.Optional;

import com.microservicesix.login.domain.model.User;

public interface ILoginRepository {

    Optional<User> findByEmail(String email);
}
