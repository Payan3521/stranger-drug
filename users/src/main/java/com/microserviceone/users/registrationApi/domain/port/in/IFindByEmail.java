package com.microserviceone.users.registrationApi.domain.port.in;

import java.util.Optional;
import com.microserviceone.users.registrationApi.domain.model.User;

public interface IFindByEmail {
    Optional<User> findByEmail(String email);
}