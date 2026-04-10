package com.example.projectuiprototype.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AnnouncementApi {
    @GET("announcements")
    Call<List<AnnouncementDto>> getAnnouncements();

    @POST("announcements")
    Call<AnnouncementDto> addAnnouncement(@Body AnnouncementDto announcement);

    @GET("announcements/latest")
    Call<AnnouncementDto> getLatestAnnouncement();

    @DELETE("announcements/{id}")
    Call<Void> deleteAnnouncement(@Path("id") String id);
}