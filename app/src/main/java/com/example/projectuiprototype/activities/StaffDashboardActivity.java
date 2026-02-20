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

public class StaffDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_staff_dashboard);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cardAnnouncements), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Button btnViewAll = findViewById(R.id.btnViewAllAnnouncements);
        Button btnAnnouncements = findViewById(R.id.btnAnnouncements);
        Button btnInventory = findViewById(R.id.btnInventory);


        btnViewAll.setOnClickListener(v -> {
            Intent intent = new Intent(StaffDashboardActivity.this, AnnouncementsActivity.class);
            startActivity(intent);
        });




        btnAnnouncements.setOnClickListener(v -> {
            Intent intent = new Intent(StaffDashboardActivity.this, AnnouncementsActivity.class);
            startActivity(intent);
        });


        btnInventory.setOnClickListener(v -> {
            Intent intent = new Intent(StaffDashboardActivity.this, InventoryActivity.class);
            startActivity(intent);
        });

        Button btnMySchedule = findViewById(R.id.btnSchedule);
        btnMySchedule.setOnClickListener(v -> {
            Intent intent = new Intent(StaffDashboardActivity.this, MyScheduleActivity.class);
            startActivity(intent);
        });


    }
}
