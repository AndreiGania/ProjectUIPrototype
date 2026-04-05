package com.example.projectuiprototype.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAnnouncementActivity extends AppCompatActivity {

    private EditText etTitle, etMessage;
    private Button btnPublish, btnCancel;

    private AnnouncementApi announcementApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_announcement);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cardView2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etTitle = findViewById(R.id.etTitle);
        etMessage = findViewById(R.id.etMessage);
        btnPublish = findViewById(R.id.btnPublish);
        btnCancel = findViewById(R.id.btnCancel);

        announcementApi = ApiClient.getClient(this).create(AnnouncementApi.class);

        btnPublish.setOnClickListener(v -> publishAnnouncement());

        btnCancel.setOnClickListener(v -> finish());
    }

    private void publishAnnouncement() {
        String title = etTitle.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Title required");
            return;
        }

        if (message.isEmpty()) {
            etMessage.setError("Message required");
            return;
        }

        AnnouncementDto payload = new AnnouncementDto();
        payload.title = title;
        payload.message = message;

        announcementApi.addAnnouncement(payload).enqueue(new Callback<AnnouncementDto>() {
            @Override
            public void onResponse(Call<AnnouncementDto> call, Response<AnnouncementDto> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(PostAnnouncementActivity.this,
                            "Publish failed: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(PostAnnouncementActivity.this,
                        "Announcement published",
                        Toast.LENGTH_SHORT).show();

                etTitle.setText("");
                etMessage.setText("");

                Intent intent = new Intent(PostAnnouncementActivity.this, AnnouncementsActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<AnnouncementDto> call, Throwable t) {
                Toast.makeText(PostAnnouncementActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}