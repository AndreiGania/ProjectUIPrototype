package com.example.projectuiprototype;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectuiprototype.database.DatabaseClient;
import com.example.projectuiprototype.models.User;

import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    UserAdapter adapter;
    List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        recyclerView = findViewById(R.id.recyclerUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadUsers();
    }

    private void loadUsers() {
        // Fetch employees only (not managers)
        userList = DatabaseClient.getInstance(this)
                .getDatabase()
                .userDao()
                .getAllUsers();

        adapter = new UserAdapter(userList, this);
        recyclerView.setAdapter(adapter);
    }
}
