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
import com.example.projectuiprototype.dao.AnnouncementDao;
import com.example.projectuiprototype.database.AppDatabase;
import com.example.projectuiprototype.database.DatabaseClient;
import com.example.projectuiprototype.models.Announcement;

public class PostAnnouncementActivity extends AppCompatActivity {

    private EditText etTitle, etMessage;
    private Button btnPublish, btnCancel;

    private AnnouncementDao announcementDao;

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

        // Views
        etTitle = findViewById(R.id.etTitle);
        etMessage = findViewById(R.id.etMessage);
        btnPublish = findViewById(R.id.btnPublish);
        btnCancel = findViewById(R.id.btnCancel);

        // DB + DAO
        AppDatabase db = DatabaseClient
                .getInstance(getApplicationContext())
                .getDatabase();
        announcementDao = db.announcementDao();

        // Publish button
        btnPublish.setOnClickListener(v -> {
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

            // Build Announcement entity
            Announcement a = new Announcement();
            // change these if your fields are named differently
            a.title = title;
            a.message = message;

            // Save to database
            announcementDao.postAnnouncement(a);

            Toast.makeText(this, "Announcement published", Toast.LENGTH_SHORT).show();

            etTitle.setText("");
            etMessage.setText("");

            // Go straight to the Announcements screen
            Intent intent = new Intent(PostAnnouncementActivity.this, AnnouncementsActivity.class);
            startActivity(intent);
            finish();
        });

        // Cancel button
        btnCancel.setOnClickListener(v -> finish());
    }
}
