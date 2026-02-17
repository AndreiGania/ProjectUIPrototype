package com.example.projectuiprototype.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectuiprototype.R;
import com.example.projectuiprototype.api.ApiClient;
import com.example.projectuiprototype.api.InventoryApi;
import com.example.projectuiprototype.api.InventoryItemDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryEditActivity extends AppCompatActivity {

    EditText itemName, itemQty;
    Button saveBtn;

    private String editingId = null;

    private InventoryApi inventoryApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_edit);

        itemName = findViewById(R.id.inputName);
        itemQty  = findViewById(R.id.inputQty);
        saveBtn  = findViewById(R.id.btnSaveItem);

        inventoryApi = ApiClient.getClient(this).create(InventoryApi.class);

        if (getIntent().hasExtra("id")) {
            editingId = getIntent().getStringExtra("id");
            itemName.setText(getIntent().getStringExtra("name"));

            // qty might be sent as double now; handle both cases safely
            if (getIntent().hasExtra("qty")) {
                try {
                    itemQty.setText(String.valueOf(getIntent().getDoubleExtra("qty", 0)));
                } catch (Exception e) {
                    itemQty.setText(getIntent().getStringExtra("qty"));
                }
            }
        }

        saveBtn.setOnClickListener(v -> save());
    }

    private void save() {
        String name = itemName.getText().toString().trim();
        String qtyStr = itemQty.getText().toString().trim();

        if (name.isEmpty() || qtyStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double qty;
        try {
            qty = Double.parseDouble(qtyStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Quantity must be a number", Toast.LENGTH_SHORT).show();
            return;
        }

        InventoryItemDto payload = new InventoryItemDto();
        payload.name = name;
        payload.quantity = qty;

        if (editingId == null || editingId.isEmpty()) {
            inventoryApi.addItem(payload).enqueue(new Callback<InventoryItemDto>() {
                @Override
                public void onResponse(Call<InventoryItemDto> call, Response<InventoryItemDto> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(InventoryEditActivity.this, "Add failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(InventoryEditActivity.this, "Item added!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Call<InventoryItemDto> call, Throwable t) {
                    Toast.makeText(InventoryEditActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            inventoryApi.updateItem(editingId, payload).enqueue(new Callback<InventoryItemDto>() {
                @Override
                public void onResponse(Call<InventoryItemDto> call, Response<InventoryItemDto> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(InventoryEditActivity.this, "Update failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(InventoryEditActivity.this, "Item updated!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Call<InventoryItemDto> call, Throwable t) {
                    Toast.makeText(InventoryEditActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
