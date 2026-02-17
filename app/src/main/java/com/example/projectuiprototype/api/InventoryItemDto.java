package com.example.projectuiprototype.api;

import com.google.gson.annotations.SerializedName;

public class InventoryItemDto {
    @SerializedName("_id")
    public String id;

    public String name;
    public double quantity;
    public String unit;
    public double lowStockThreshold;
}
