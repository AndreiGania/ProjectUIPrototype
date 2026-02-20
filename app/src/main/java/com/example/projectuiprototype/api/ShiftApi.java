package com.example.projectuiprototype.api;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ShiftApi {

    @GET("shifts")
    Call<List<ShiftDto>> getShifts();

    @POST("shifts")
    Call<ShiftDto> createShift(@Body CreateShiftRequest body);
}