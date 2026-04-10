package com.example.projectuiprototype.api;

public class CreateShiftRequest {
    public String employeeId;
    public String employeeUsername;
    public String start;
    public String end;
    public String position;
    public String notes;

    public CreateShiftRequest(String employeeId, String employeeUsername, String start, String end, String position, String notes) {
        this.employeeId = employeeId;
        this.employeeUsername = employeeUsername;
        this.start = start;
        this.end = end;
        this.position = position;
        this.notes = notes;
    }
}