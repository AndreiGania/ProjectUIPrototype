package com.example.projectuiprototype.activities;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectuiprototype.R;
import com.example.projectuiprototype.api.AnnouncementApi;
import com.example.projectuiprototype.api.AnnouncementDto;
import com.example.projectuiprototype.api.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnnouncementsActivity extends AppCompatActivity {

    private TextView tvAnnouncementsList;
    private AnnouncementApi announcementApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_announcements_acvtivity);

        View root = findViewById(R.id.announcementCard);

        tvAnnouncementsList = findViewById(R.id.tvAnnouncementsList);

        announcementApi = ApiClient.getClient(this).create(AnnouncementApi.class);

        loadAnnouncements();
    }

    private void loadAnnouncements() {
        announcementApi.getAnnouncements().enqueue(new Callback<List<AnnouncementDto>>() {
            @Override
            public void onResponse(Call<List<AnnouncementDto>> call, Response<List<AnnouncementDto>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    tvAnnouncementsList.setText("Failed to load announcements.");
                    Toast.makeText(
                            AnnouncementsActivity.this,
                            "Load failed: " + response.code(),
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                List<AnnouncementDto> announcements = response.body();

                if (announcements.isEmpty()) {
                    tvAnnouncementsList.setText("No announcements yet.");
                    return;
                }

                StringBuilder sb = new StringBuilder();

                for (AnnouncementDto a : announcements) {
                    String title = a.title == null ? "" : a.title;
                    String message = a.message == null ? "" : a.message;

                    sb.append("• <b>")
                            .append(title)
                            .append("</b><br>")
                            .append(message)
                            .append("<br><br>");
                }

                tvAnnouncementsList.setText(
                        Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
                );
            }

            @Override
            public void onFailure(Call<List<AnnouncementDto>> call, Throwable t) {
                tvAnnouncementsList.setText("Network error loading announcements.");
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