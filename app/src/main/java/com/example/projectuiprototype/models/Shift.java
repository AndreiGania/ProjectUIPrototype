package com.example.projectuiprototype.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shifts")
public class Shift {

    @PrimaryKey(autoGenerate = true)
    public int id;

    // e.g. "Morning Shift" or "Monday"
    public String day;

    // e.g. "9:00 AM – 5:00 PM"
    public String time;

    // id of the user this shift belongs to
    public int userId;
}
