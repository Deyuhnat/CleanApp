package com.example.cleanproject;

public class User {
    private String email;
    private String name;
    private String role;
    private String phone;

    // Default constructor is required for Firebase data to object conversion
    public User() {
    }

    // Constructor with parameters
    public User(String email, String name, String role, String phone) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.phone = phone;
    }

    // Getters and setters for each field
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}