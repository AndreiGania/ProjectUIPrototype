package com.example.projectuiprototype.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projectuiprototype.R;
import com.example.projectuiprototype.api.AnnouncementApi;
import com.example.projectuiprototype.api.AnnouncementDto;
import com.example.projectuiprototype.api.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffDashboardActivity extends AppCompatActivity {

    private TextView txtLatestAnnouncementTitle;
    private TextView txtLatestAnnouncementMessage;

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

        txtLatestAnnouncementTitle = findViewById(R.id.txtLatestAnnouncementTitle);
        txtLatestAnnouncementMessage = findViewById(R.id.txtLatestAnnouncementMessage);

        Button btnViewAll = findViewById(R.id.btnViewAllAnnouncements);
        Button btnAnnouncements = findViewById(R.id.btnAnnouncements);
        Button btnInventory = findViewById(R.id.btnInventory);
        Button btnMySchedule = findViewById(R.id.btnSchedule);

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

        btnMySchedule.setOnClickListener(v -> {
            Intent intent = new Intent(StaffDashboardActivity.this, MyScheduleActivity.class);
            startActivity(intent);
        });

        loadLatestAnnouncement();
    }

    private void loadLatestAnnouncement() {
        AnnouncementApi api = ApiClient.getClient(this).create(AnnouncementApi.class);

        api.getLatestAnnouncement().enqueue(new Callback<AnnouncementDto>() {
            @Override
            public void onResponse(Call<AnnouncementDto> call, Response<AnnouncementDto> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    txtLatestAnnouncementMessage.setText("No announcements available.");
                    return;
                }

                AnnouncementDto latest = response.body();

                txtLatestAnnouncementTitle.setText(
                        latest.title != null ? latest.title : "Announcements"
                );

                txtLatestAnnouncementMessage.setText(
                        latest.message != null ? latest.message : "No message available."
                );
            }

            @Override
            public void onFailure(Call<AnnouncementDto> call, Throwable t) {
                txtLatestAnnouncementMessage.setText("Failed to load announcement.");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLatestAnnouncement();
    }
}