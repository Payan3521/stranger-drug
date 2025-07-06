package com.gateway.gateway.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserContext {
    private final String userId;
    private final String email;
    private final String role;
    private final boolean isAuthenticated;
    
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
