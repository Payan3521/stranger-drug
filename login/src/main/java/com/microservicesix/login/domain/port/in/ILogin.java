package com.microservicesix.login.domain.port.in;

import com.microservicesix.login.domain.model.LoginRequest;
import com.microservicesix.login.domain.model.LoginResponse;

public interface ILogin {

    LoginResponse login(LoginRequest loginRequest);
    
}
