package com.example.projectuiprototype.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectuiprototype.R;
import com.example.projectuiprototype.dao.InventoryDao;
import com.example.projectuiprototype.database.DatabaseClient;
import com.example.projectuiprototype.models.InventoryItem;

import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    InventoryAdapter adapter;
    List<InventoryItem> itemList;
    Button addItemBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        recyclerView = findViewById(R.id.inventoryRecycler);
        addItemBtn = findViewById(R.id.btnAddItem);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadInventory();

        addItemBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, InventoryEditActivity.class));
        });
    }

    private void loadInventory() {
        InventoryDao dao = DatabaseClient.getInstance(this).getDatabase().inventoryDao();
        itemList = dao.getAllItems();

        adapter = new InventoryAdapter(itemList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInventory();
    }
}
