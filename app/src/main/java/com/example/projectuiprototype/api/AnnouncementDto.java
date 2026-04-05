package com.example.projectuiprototype.api;

import com.google.gson.annotations.SerializedName;

public class AnnouncementDto {
    @SerializedName("_id")
    public String id;

    public String title;
    public String message;
    public String createdAt;
    public String updatedAt;
}