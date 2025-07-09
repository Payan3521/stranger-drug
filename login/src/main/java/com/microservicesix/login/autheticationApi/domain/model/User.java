package com.microservicesix.login.autheticationApi.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private UserRole role;
    private boolean verifiedCode;
    private boolean verifiedTerm;
    private boolean active;

    public enum UserRole {
        CUSTOMER("Customer"),
        ADMIN("Administrator");

        private final String displayName;

        UserRole(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean isCustomer() {
        return role == UserRole.CUSTOMER;
    }

    public boolean canLogin() {
        return active && verifiedCode && verifiedTerm;
    }
}
