package com.vehicle.model;

import com.vehicle.model.enums.UserType;

import java.time.LocalDate;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private UserType userType;
    private LocalDate birthDate;

    public void getUserType() {
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    // Constructors, getters, setters
}
