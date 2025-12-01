package com.example.projectuiprototype.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectuiprototype.R;
import com.example.projectuiprototype.dao.InventoryDao;
import com.example.projectuiprototype.database.DatabaseClient;
import com.example.projectuiprototype.models.InventoryItem;

public class InventoryEditActivity extends AppCompatActivity {

    EditText itemName,itemQty;
    Button saveBtn;
    InventoryItem editingItem=null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_edit);

        itemName=findViewById(R.id.inputName);
        itemQty=findViewById(R.id.inputQty);
        saveBtn=findViewById(R.id.btnSaveItem);

        if(getIntent().hasExtra("id")){
            editingItem=new InventoryItem();
            editingItem.id=getIntent().getIntExtra("id",0);
            itemName.setText(getIntent().getStringExtra("name"));
            itemQty.setText(getIntent().getStringExtra("qty"));
        }

        saveBtn.setOnClickListener(v -> save());
    }

    private void save(){
        InventoryDao dao= DatabaseClient.getInstance(this).getDatabase().inventoryDao();

        if(editingItem==null){
            InventoryItem newItem=new InventoryItem();
            newItem.name=itemName.getText().toString();
            newItem.quantity=itemQty.getText().toString();
            dao.addItem(newItem);
            Toast.makeText(this,"Item added!",Toast.LENGTH_SHORT).show();
        } else {
            editingItem.name=itemName.getText().toString();
            editingItem.quantity=itemQty.getText().toString();
            dao.updateItem(editingItem);
            Toast.makeText(this,"Item updated!",Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
