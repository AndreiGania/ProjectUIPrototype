package com.example.projectuiprototype.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.projectuiprototype.models.InventoryItem;
import java.util.List;

@Dao
public interface InventoryDao {

    @Insert
    void addItem(InventoryItem item);

    @Update
    void updateItem(InventoryItem item);

    @Delete
    void deleteItem(InventoryItem item);

    @Query("SELECT * FROM inventory")
    List<InventoryItem> getAllItems();

    @Query("SELECT * FROM inventory WHERE name LIKE '%' || :search || '%'")
    List<InventoryItem> searchItems(String search);
}
