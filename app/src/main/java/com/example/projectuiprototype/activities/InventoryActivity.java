package com.example.projectuiprototype.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectuiprototype.R;
import com.example.projectuiprototype.database.DatabaseClient;
import com.example.projectuiprototype.models.InventoryItem;

import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    InventoryAdapter adapter;
    EditText searchInput;
    List<InventoryItem> fullList;  // All items
    List<InventoryItem> filteredList; // Search result

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        recyclerView = findViewById(R.id.inventoryRecycler);
        Button addItemBtn = findViewById(R.id.btnAddItem);
        searchInput = findViewById(R.id.searchInput);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadInventory();  // load full list

        addItemBtn.setOnClickListener(v ->
                startActivity(new Intent(this, InventoryEditActivity.class))
        );

        // 🔥 Instant Live Search
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int i,int i1,int i2){}
            @Override public void afterTextChanged(Editable s){}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItems(s.toString());
            }
        });
    }

    private void loadInventory(){
        fullList = DatabaseClient.getInstance(this).getDatabase().inventoryDao().getAllItems();
        adapter = new InventoryAdapter(fullList, this);
        recyclerView.setAdapter(adapter);
    }

    private void filterItems(String text){
        filteredList = DatabaseClient.getInstance(this).getDatabase().inventoryDao().searchItems(text);
        adapter = new InventoryAdapter(filteredList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInventory();
    }
}
