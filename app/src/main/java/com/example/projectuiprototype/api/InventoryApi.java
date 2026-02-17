package com.example.projectuiprototype.api;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface InventoryApi {
    @GET("inventory")
    Call<List<InventoryItemDto>> getInventory();
    @DELETE("inventory/{id}")
    Call<Void> deleteItem(@Path("id") String id);

    @POST("inventory")
    Call<InventoryItemDto> addItem(@Body InventoryItemDto item);

    @PUT("inventory/{id}")
    Call<InventoryItemDto> updateItem(@Path("id") String id, @Body InventoryItemDto item);


}
