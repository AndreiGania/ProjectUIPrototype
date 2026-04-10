package com.example.projectuiprototype.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectuiprototype.R;
import com.example.projectuiprototype.api.AnnouncementApi;
import com.example.projectuiprototype.api.AnnouncementDto;
import com.example.projectuiprototype.api.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnnouncementsActivity extends AppCompatActivity {

    private RecyclerView recyclerAnnouncements;
    private AnnouncementAdapter adapter;
    private AnnouncementApi announcementApi;
    private final List<AnnouncementDto> announcementList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_announcements_acvtivity);

        recyclerAnnouncements = findViewById(R.id.recyclerAnnouncements);
        recyclerAnnouncements.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AnnouncementAdapter(this, announcementList);
        recyclerAnnouncements.setAdapter(adapter);

        announcementApi = ApiClient.getClient(this).create(AnnouncementApi.class);

        loadAnnouncements();
    }

    private void loadAnnouncements() {
        announcementApi.getAnnouncements().enqueue(new Callback<List<AnnouncementDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<AnnouncementDto>> call, @NonNull Response<List<AnnouncementDto>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(
                            AnnouncementsActivity.this,
                            "Load failed: " + response.code(),
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                announcementList.clear();
                announcementList.addAll(response.body());
                adapter.notifyDataSetChanged();

                if (announcementList.isEmpty()) {
                    Toast.makeText(
                            AnnouncementsActivity.this,
                            "No announcements yet.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AnnouncementDto>> call, @NonNull Throwable t) {
                Toast.makeText(
                        AnnouncementsActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAnnouncements();
    }
}