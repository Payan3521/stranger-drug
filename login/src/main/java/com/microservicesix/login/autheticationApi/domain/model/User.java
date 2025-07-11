package com.microservicesix.login.autheticationApi.domain.model;

import java.util.Objects;

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


    public User(Long id, String name, String lastName, String email, String password, String phone,
                UserRole role, boolean verified_code, boolean verified_term){
        this.id = id;
        this.name = Objects.requireNonNull("Name cannot be null");
        this.lastName = Objects.requireNonNull("LastName cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.password = Objects.requireNonNull(password, "Password cannot be null");
        this.phone = Objects.requireNonNull(phone, "Phone cannot be null");
        this.role = Objects.requireNonNull(role, "Role cannot be null");
        this.verifiedCode = verified_code;
        this.verifiedTerm = verified_term;

        validateUserData();
    }


    private void validateUserData(){
        validateName(name);
        validateLastName(lastName);
        validateEmail(email);
        validatePassword(password);
        validatePhone(phone);
        validateSpecificData();
    }
    private void validateName(String name){
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (name.length() < 2 || name.length() > 50 ) {
            throw new IllegalArgumentException("Name must");
        }
        if (name.matches("^[a-zA-ZÀ-ÿ\\s]+$")) {
            throw new IllegalArgumentException("Name can only contain letters and spaces");
        }
    }

    public void validateSpecificData(){

    }

    private void validateLastName(String lastName){
        if (lastName == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        if (lastName.length() <  2 || lastName.length() > 50) {
            throw new IllegalArgumentException("Last name must be between 2 and 50 characters");
        }
        if (lastName.matches("^[a-zA-ZÀ-ÿ\\s]+$")) {
            throw new IllegalArgumentException("Last name can only contain letters and spaces");
        }
    }

    private void validateEmail(String email){
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        // In production, add more sophisticated password validation
    }

    private void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty");
        }
        if (!phone.matches("^\\+?[0-9]{10,15}$")) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }

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
