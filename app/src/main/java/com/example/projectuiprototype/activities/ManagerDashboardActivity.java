package com.example.projectuiprototype.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projectuiprototype.R;

public class ManagerDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutActions), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnManageShifts = findViewById(R.id.btnManageShifts);
        Button btnPostAnnouncement = findViewById(R.id.btnPostAnnouncement);
        Button btnViewAnnouncements = findViewById(R.id.btnViewAnnouncements);
        Button btnInventory = findViewById(R.id.btnInventory);
        Button btnManageUsers = findViewById(R.id.btnManageUsers);

        btnManageShifts.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerDashboardActivity.this, ManageShiftsActivity.class);
            startActivity(intent);
        });

        btnPostAnnouncement.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerDashboardActivity.this, PostAnnouncementActivity.class);
            startActivity(intent);
        });

        btnViewAnnouncements.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerDashboardActivity.this, AnnouncementsActivity.class);
            startActivity(intent);
        });

        btnInventory.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerDashboardActivity.this, InventoryActivity.class);
            startActivity(intent);
        });

        btnManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerDashboardActivity.this, ManageUsersActivity.class);
            startActivity(intent);
        });
    }
}