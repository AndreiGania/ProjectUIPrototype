package com.example.projectuiprototype.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projectuiprototype.R;
import com.example.projectuiprototype.dao.ShiftDao;
import com.example.projectuiprototype.database.AppDatabase;
import com.example.projectuiprototype.database.DatabaseClient;
import com.example.projectuiprototype.models.Shift;

import java.util.List;

public class MyScheduleActivity extends AppCompatActivity {

    private TextView tvScheduleContent;
    private Button btnRequestChange;

    private ShiftDao shiftDao;

    // For now, use a demo user id.
    // In a full version you would pass the logged-in user's id here.
    private static final int DEMO_USER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_schedule);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvScheduleContent = findViewById(R.id.tvScheduleContent);
        btnRequestChange = findViewById(R.id.btnRequestChange);

        // Get DB + DAO
        AppDatabase db = DatabaseClient
                .getInstance(getApplicationContext())
                .getDatabase();
        shiftDao = db.shiftDao();

        // Load the schedule for this user
        loadSchedule();

        // Keep your existing request-change dialog
        btnRequestChange.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Shift Change Request")
                    .setMessage("Your shift change request has been sent to the manager.")
                    .setPositiveButton("OK", null)
                    .show();
        });
    }

    private void loadSchedule() {
        List<Shift> shifts = shiftDao.getShiftsForUser(DEMO_USER_ID);

        if (shifts == null || shifts.isEmpty()) {
            tvScheduleContent.setText("You have no shifts scheduled yet.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Shift shift : shifts) {
            sb.append(shift.day)      // e.g. "Monday" or "Morning Shift"
                    .append(" – ")
                    .append(shift.time)     // e.g. "9:00 AM – 5:00 PM"
                    .append("\n");
        }

        tvScheduleContent.setText(sb.toString());
    }
}
