package com.example.projectuiprototype.api;

public class CreateShiftRequest {
    public String employeeId;
    public String employeeUsername;
    public String start; // ISO datetime string
    public String end;   // ISO datetime string

    public CreateShiftRequest(String employeeId, String employeeUsername, String start, String end) {
        this.employeeId = employeeId;
        this.employeeUsername = employeeUsername;
        this.start = start;
        this.end = end;
    }
}