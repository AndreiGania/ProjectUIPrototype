package com.example.projectuiprototype.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class ManageShiftsActivity extends AppCompatActivity {

    private EditText etShiftName;
    private EditText etShiftTime;
    private Button btnAddShift;
    private Button btnViewShifts;

    private ShiftDao shiftDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_shifts);

        // keep your insets behavior
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- views from XML ---
        etShiftName = findViewById(R.id.etShiftName);
        etShiftTime = findViewById(R.id.etShiftTime);
        btnAddShift = findViewById(R.id.btnAddShift);
        btnViewShifts = findViewById(R.id.btnViewShifts);

        // --- get database + DAO (Room) ---
        AppDatabase db = DatabaseClient
                .getInstance(getApplicationContext())
                .getDatabase();
        shiftDao = db.shiftDao();

        // ========== ADD SHIFT ==========
        btnAddShift.setOnClickListener(v -> {
            String name = etShiftName.getText().toString().trim();
            String time = etShiftTime.getText().toString().trim();

            if (name.isEmpty()) {
                etShiftName.setError("Shift name required");
                return;
            }
            if (time.isEmpty()) {
                etShiftTime.setError("Shift time required");
                return;
            }

            // build Shift entity
            Shift shift = new Shift();
            shift.day = name;   // store the "Shift Name" into 'day'
            shift.time = time;
            shift.userId = 1;   // or the logged-in user id if you have it

            // allowMainThreadQueries() is enabled in DatabaseClient,
            // so this is okay for a small demo
            shiftDao.addShift(shift);

            Toast.makeText(this, "Shift added", Toast.LENGTH_SHORT).show();

            etShiftName.setText("");
            etShiftTime.setText("");
        });

        // ========== VIEW ALL SHIFTS ==========
        btnViewShifts.setOnClickListener(v -> {
            List<Shift> shifts = shiftDao.getAllShifts();

            if (shifts == null || shifts.isEmpty()) {
                Toast.makeText(this, "No shifts saved yet", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder sb = new StringBuilder();
            int index = 1;
            for (Shift s : shifts) {
                sb.append(index++)
                        .append(". ")
                        .append(s.day)   // show the name/day
                        .append(" – ")
                        .append(s.time)  // show the time
                        .append("\n");
            }

            new AlertDialog.Builder(this)
                    .setTitle("All Shifts (from database)")
                    .setMessage(sb.toString())
                    .setPositiveButton("OK", null)
                    .show();
        });
    }
}
