package com.example.projectuiprototype.activities;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

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

import java.util.List;

public class AnnouncementsActivity extends AppCompatActivity {

    private TextView tvAnnouncementsList;
    private AnnouncementDao announcementDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // ✅ IMPORTANT: make sure this layout name matches your xml file exactly
        setContentView(R.layout.activity_announcements_acvtivity);

        // ✅ Only apply insets if the view exists (prevents crash)
        View root = findViewById(R.id.announcementCard);
        if (root != null) {
//            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
//                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//                return insets;
//            });
        }

        tvAnnouncementsList = findViewById(R.id.tvAnnouncementsList);

        // ✅ DB + DAO (Room)
        AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getDatabase();
        announcementDao = db.announcementDao();

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
            String title = (a.title == null) ? "" : a.title;
            String msg = (a.message == null) ? "" : a.message;

            sb.append("• <b>")
                    .append(title)
                    .append("</b><br>")
                    .append(msg)
                    .append("<br><br>");
        }

        tvAnnouncementsList.setText(
                Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
        );
    }
}