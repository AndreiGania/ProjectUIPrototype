package com.example.projectuiprototype.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.projectuiprototype.models.Announcement;
import java.util.List;

@Dao
public interface AnnouncementDao {

    @Insert
    void postAnnouncement(Announcement announcement);

    @Query("SELECT * FROM announcements ORDER BY id DESC")
    List<Announcement> getAllAnnouncements();
}
