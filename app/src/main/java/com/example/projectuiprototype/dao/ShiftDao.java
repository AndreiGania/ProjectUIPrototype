package com.example.projectuiprototype.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.projectuiprototype.models.Shift;
import java.util.List;

@Dao
public interface ShiftDao {

    @Insert
    void addShift(Shift shift);

    @Query("SELECT * FROM shifts WHERE userId = :userId")
    List<Shift> getShiftsForUser(int userId);

    @Query("SELECT * FROM shifts")
    List<Shift> getAllShifts();
}
