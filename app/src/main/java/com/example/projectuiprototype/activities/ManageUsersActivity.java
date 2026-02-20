package com.example.projectuiprototype.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectuiprototype.R;
import com.example.projectuiprototype.api.ApiClient;
import com.example.projectuiprototype.api.UserApi;
import com.example.projectuiprototype.api.UserDto;
import com.example.projectuiprototype.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageUsersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    UserAdapter adapter;

    private UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        // manager-only screen
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String role = prefs.getString("role", "");
        if (!"manager".equals(role)) {
            Toast.makeText(this, "Access denied — Manager only", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userApi = ApiClient.getClient(getApplicationContext()).create(UserApi.class);

        loadUsersFromServer();
    }

    private void loadUsersFromServer() {
        userApi.getUsers().enqueue(new Callback<List<UserDto>>() {
            @Override
            public void onResponse(Call<List<UserDto>> call, Response<List<UserDto>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(ManageUsersActivity.this,
                            "Failed to load users: " + response.code(),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                List<UserDto> all = response.body();

                // show ONLY users who are NOT manager/admin
                List<User> promotable = new ArrayList<>();
                for (UserDto u : all) {
                    String r = (u.role == null) ? "" : u.role.toLowerCase();
                    if (!r.equals("manager") && !r.equals("admin")) {
                        User local = new User();
                        local.serverId = u.id; // Mongo id from API
                        local.username = u.username;
                        local.name = u.name;
                        local.email = u.email;
                        local.role = u.role;
                        promotable.add(local);
                    }
                }

                adapter = new UserAdapter(promotable, ManageUsersActivity.this, () -> loadUsersFromServer());
                recyclerView.setAdapter(adapter);

                if (promotable.isEmpty()) {
                    Toast.makeText(ManageUsersActivity.this, "No users available to promote", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserDto>> call, Throwable t) {
                Toast.makeText(ManageUsersActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}