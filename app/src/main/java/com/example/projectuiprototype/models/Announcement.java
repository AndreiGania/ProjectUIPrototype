package com.example.projectuiprototype.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "announcements")
public class Announcement {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String message;
    public String datePosted;
}

