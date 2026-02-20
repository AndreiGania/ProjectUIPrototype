package com.example.projectuiprototype.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface UserApi {
    @GET("users")
    Call<List<UserDto>> getUsers();

    @PATCH("users/{id}/promote")
    Call<UserDto> promoteToManager(@Path("id") String userId);
}