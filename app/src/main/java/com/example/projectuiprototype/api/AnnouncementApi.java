package com.example.projectuiprototype.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AnnouncementApi {
    @GET("announcements")
    Call<List<AnnouncementDto>> getAnnouncements();

    @POST("announcements")
    Call<AnnouncementDto> addAnnouncement(@Body AnnouncementDto announcement);
}