package com.example.projectuiprototype.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectuiprototype.R;
import com.example.projectuiprototype.api.ApiClient;
import com.example.projectuiprototype.api.InventoryApi;
import com.example.projectuiprototype.api.InventoryItemDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    InventoryAdapter adapter;
    EditText searchInput;

    private final List<InventoryItemDto> fullList = new ArrayList<>();
    private final List<InventoryItemDto> filteredList = new ArrayList<>();

    private InventoryApi inventoryApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        recyclerView = findViewById(R.id.inventoryRecycler);
        Button addItemBtn = findViewById(R.id.btnAddItem);
        searchInput = findViewById(R.id.searchInput);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        inventoryApi = ApiClient.getClient(this).create(InventoryApi.class);

        adapter = new InventoryAdapter(filteredList, this);
        recyclerView.setAdapter(adapter);

        loadInventoryFromApi();

        addItemBtn.setOnClickListener(v ->
                startActivity(new Intent(this, InventoryEditActivity.class))
        );

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int i,int i1,int i2){}
            @Override public void afterTextChanged(Editable s){}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItemsLocal(s.toString());
            }
        });
    }

    private void loadInventoryFromApi() {
        inventoryApi.getInventory().enqueue(new Callback<List<InventoryItemDto>>() {
            @Override
            public void onResponse(Call<List<InventoryItemDto>> call, Response<List<InventoryItemDto>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(InventoryActivity.this, "Load failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                fullList.clear();
                fullList.addAll(response.body());

                // default view = full list
                filterItemsLocal(searchInput.getText().toString());
            }

            @Override
            public void onFailure(Call<List<InventoryItemDto>> call, Throwable t) {
                Toast.makeText(InventoryActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterItemsLocal(String text) {
        filteredList.clear();

        String q = text == null ? "" : text.trim().toLowerCase();

        if (q.isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            for (InventoryItemDto item : fullList) {
                if (item.name != null && item.name.toLowerCase().contains(q)) {
                    filteredList.add(item);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInventoryFromApi();
    }
}
