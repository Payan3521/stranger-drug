package com.microserviceone.users.registrationApi.domain.port.out;

import java.util.List;
import java.util.Optional;
import com.microserviceone.users.registrationApi.domain.model.User;

public interface IRegisterRepository {
    User save(User user);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findById(Long id);
    Optional<User> delete(Long id);
    Optional<User> update(Long id, User user);
    List<User> findByFilters(String name, String lastName, String rol);
}