package com.example.projectuiprototype.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.projectuiprototype.models.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void registerUser(User user);

    // Login verification
    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    User login(String username, String password);

    // Check if username already exists
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    // Get all (for manager view later if needed)
    @Query("SELECT * FROM users")
    List<User> getAllUsers();
}
