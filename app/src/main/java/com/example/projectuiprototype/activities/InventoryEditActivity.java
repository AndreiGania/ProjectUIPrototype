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

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryEditActivity extends AppCompatActivity {

    EditText itemName, itemQty;
    Button saveBtn;
    Button btnQtyMinus, btnQtyPlus;

    private String editingId = null;
    private InventoryApi inventoryApi;

    private static final double STEP = 1.0;
    private final DecimalFormat df = new DecimalFormat("0.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_edit);

        itemName = findViewById(R.id.inputName);
        itemQty  = findViewById(R.id.inputQty);
        saveBtn  = findViewById(R.id.btnSaveItem);

        btnQtyMinus = findViewById(R.id.btnQtyMinus);
        btnQtyPlus  = findViewById(R.id.btnQtyPlus);

        inventoryApi = ApiClient.getClient(this).create(InventoryApi.class);

        if (getIntent().hasExtra("id")) {
            editingId = getIntent().getStringExtra("id");
            itemName.setText(getIntent().getStringExtra("name"));

            if (getIntent().hasExtra("qty")) {
                double q = getIntent().getDoubleExtra("qty", 0);
                itemQty.setText(df.format(q));
            }
        }

        btnQtyPlus.setOnClickListener(v -> {
            double current = getQtyValue();
            current += STEP;
            setQtyValue(current);
        });

        btnQtyMinus.setOnClickListener(v -> {
            double current = getQtyValue();
            current = Math.max(0, current - STEP);
            setQtyValue(current);
        });

        saveBtn.setOnClickListener(v -> save());
    }

    private double getQtyValue() {
        try {
            return Double.parseDouble(itemQty.getText().toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private void setQtyValue(double v) {
        itemQty.setText(df.format(v));
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
                    Toast.makeText(InventoryEditActivity.this, "Item added!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Call<InventoryItemDto> call, Throwable t) {
                    Toast.makeText(InventoryEditActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            inventoryApi.updateItem(editingId, payload).enqueue(new Callback<InventoryItemDto>() {
                @Override
                public void onResponse(Call<InventoryItemDto> call, Response<InventoryItemDto> response) {
                    Toast.makeText(InventoryEditActivity.this, "Item updated!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Call<InventoryItemDto> call, Throwable t) {
                    Toast.makeText(InventoryEditActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
