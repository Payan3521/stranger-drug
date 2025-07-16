package com.microservicesix.login.autheticationApi.infraestructure.external;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
            @RequestHeader("Authorization") String authorization
    );

    @PatchMapping("/id/{id}")
    ApiResponse<UserResponse> updateLastLogin(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String authorization
    );
}