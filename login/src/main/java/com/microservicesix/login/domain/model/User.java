package com.microservicesix.login.domain.model;

import java.util.Objects;

import com.microservicesix.login.domain.exception.InvalidCredentialsException;

public class User {
    private String name;
    private String email;
    private int age;

    public User(String name, String email, int age){
        this.name = Objects.requireNonNull(name);
        this.email = Objects.requireNonNull(email);
        this.age = Objects.requireNonNull(age);
        validateCredentialsUser();;
    }

    public User() {
        
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public int getAge(){
        return age;
    }

    public void setAge(int age){
        this.age = age;
    }

    private void validateName(String name){

        if (name == null || name.trim().isEmpty()) {
            throw new InvalidCredentialsException("Name cannot be null");
        }
    }

    private void validateEmail(String email){

        if (email == null || email.trim().isEmpty()) {
            throw new InvalidCredentialsException("Email cannot be null");
        }
    }

    private void validateAge(int age){

        if (age == 0) {
            throw new InvalidCredentialsException("Age cannot be 0");
        }
    }

    private void validateCredentialsUser(){
        validateName(name);
        validateEmail(email);
        validateAge(age);
    }
}
