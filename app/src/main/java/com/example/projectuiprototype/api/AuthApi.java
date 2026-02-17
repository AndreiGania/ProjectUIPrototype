package com.example.projectuiprototype.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);
}
