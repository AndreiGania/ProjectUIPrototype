package com.example.projectuiprototype.api;

public class LoginResponse {
    public String token;
    public User user;

    public static class User {
        public String id;
        public String name;
        public String username;
        public String role;
    }
}
