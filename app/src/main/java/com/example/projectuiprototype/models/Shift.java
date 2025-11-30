package com.example.projectuiprototype.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shifts")
public class Shift {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String day;
    public String time;
    public int userId;
}
