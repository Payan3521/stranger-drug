package com.microservicesix.login.autheticationApi.infraestructure.external;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import com.microservicesix.login.autheticationApi.infraestructure.external.dto.ApiResponse;
import com.microservicesix.login.autheticationApi.infraestructure.external.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "users-service", path = "/register")
public interface UserServiceClient {
    
    @GetMapping("/email")
    ApiResponse<UserResponse> findUserByEmail(
            @RequestParam("email") String email,
            @RequestHeader("Authorization") String authorization);
}