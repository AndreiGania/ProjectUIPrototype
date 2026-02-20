package com.example.projectuiprototype.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.projectuiprototype.models.User;
import com.example.projectuiprototype.models.InventoryItem;
import com.example.projectuiprototype.models.Announcement;
import com.example.projectuiprototype.models.Shift;

import com.example.projectuiprototype.dao.*;

@Database(entities = {User.class, InventoryItem.class, Announcement.class, Shift.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract InventoryDao inventoryDao();
    public abstract AnnouncementDao announcementDao();
    public abstract ShiftDao shiftDao();
}
