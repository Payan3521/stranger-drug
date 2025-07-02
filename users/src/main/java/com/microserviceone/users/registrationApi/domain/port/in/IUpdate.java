package com.microserviceone.users.registrationApi.domain.port.in;

import java.util.Optional;
import com.microserviceone.users.registrationApi.domain.model.User;

public interface IUpdate {
    Optional<User> update(Long id, User user);
}