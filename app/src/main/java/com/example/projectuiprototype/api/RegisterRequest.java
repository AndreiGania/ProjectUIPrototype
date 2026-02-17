package com.example.projectuiprototype.api;

public class RegisterRequest {
    public String name;
    public String email;
    public String username;
    public String password;
    public String role;

    public RegisterRequest(String name, String email, String username, String password, String role) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
