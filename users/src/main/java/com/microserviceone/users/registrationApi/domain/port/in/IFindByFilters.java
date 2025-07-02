package com.microserviceone.users.registrationApi.domain.port.in;

import java.util.List;
import com.microserviceone.users.registrationApi.domain.model.User;

public interface IFindByFilters {
    List<User> findByFilters(String name, String lastName, String rol);
}