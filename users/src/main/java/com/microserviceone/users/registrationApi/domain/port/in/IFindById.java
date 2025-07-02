package com.microserviceone.users.registrationApi.domain.port.in;

import java.util.Optional;
import com.microserviceone.users.registrationApi.domain.model.User;

public interface IFindById {
    Optional<User> findById(Long id);
}