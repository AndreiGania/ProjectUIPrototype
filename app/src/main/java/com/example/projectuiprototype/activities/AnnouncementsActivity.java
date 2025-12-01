package com.example.projectuiprototype.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import android.text.Html;



import com.example.projectuiprototype.dao.AnnouncementDao;
import com.example.projectuiprototype.database.AppDatabase;
import com.example.projectuiprototype.database.DatabaseClient;
import com.example.projectuiprototype.models.Announcement;

import com.example.projectuiprototype.R;

public class AnnouncementsActivity extends AppCompatActivity {

    private TextView tvAnnouncementsList;
    private AnnouncementDao announcementDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_announcements_acvtivity);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.announcementCard), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvAnnouncementsList = findViewById(R.id.tvAnnouncementsList);

        // DB + DAO
        AppDatabase db = DatabaseClient
                .getInstance(getApplicationContext())
                .getDatabase();
        announcementDao = db.announcementDao();

        // Load and display announcements
        loadAnnouncements();
    }

    private void loadAnnouncements() {
        List<Announcement> announcements = announcementDao.getAllAnnouncements();

        if (announcements == null || announcements.isEmpty()) {
            tvAnnouncementsList.setText("No announcements yet.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Announcement a : announcements) {
            // ⚠ if your fields have different names, adjust here
            sb.append("• <b>")
                    .append(a.title)
                    .append("</b><br>");
            sb.append(a.message)
                    .append("<br><br>");
        }

        tvAnnouncementsList.setText(
                Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
        );
    }

}