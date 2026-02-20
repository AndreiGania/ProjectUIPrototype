package com.example.projectuiprototype.activities;

import android.os.Bundle;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class ManageShiftsActivity extends AppCompatActivity {

    // New fields (from the new XML)
    private TextInputEditText etPosition;
    private TextInputEditText etStartTime;
    private TextInputEditText etEndTime;

    private MaterialButton btnAddShift;
    private MaterialButton btnViewShifts;

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
        etPosition = findViewById(R.id.etPosition);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);

        btnAddShift = findViewById(R.id.btnAddShift);
        btnViewShifts = findViewById(R.id.btnViewShifts);

        // --- get database + DAO (Room) ---
        AppDatabase db = DatabaseClient
                .getInstance(getApplicationContext())
                .getDatabase();
        shiftDao = db.shiftDao();

        // ========== ADD SHIFT ==========
        btnAddShift.setOnClickListener(v -> {
            String position = getText(etPosition);
            String start = getText(etStartTime);
            String end = getText(etEndTime);

            if (position.isEmpty()) {
                etPosition.setError("Position required");
                etPosition.requestFocus();
                return;
            }
            if (start.isEmpty()) {
                etStartTime.setError("Start time required");
                etStartTime.requestFocus();
                return;
            }
            if (end.isEmpty()) {
                etEndTime.setError("End time required");
                etEndTime.requestFocus();
                return;
            }

            // Store start-end in the single 'time' field you already have
            String timeRange = start + " - " + end;

            Shift shift = new Shift();
            shift.day = position;     // reuse 'day' as "Position"
            shift.time = timeRange;   // store "Start - End"
            shift.userId = 1;         // replace with your logged-in user id if you have it

            shiftDao.addShift(shift);

            Toast.makeText(this, "Shift added", Toast.LENGTH_SHORT).show();

            etPosition.setText("");
            etStartTime.setText("");
            etEndTime.setText("");
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
                        .append(s.day)     // Position
                        .append(" — ")
                        .append(s.time)    // Start - End
                        .append("\n");
            }

            new AlertDialog.Builder(this)
                    .setTitle("All Shifts")
                    .setMessage(sb.toString())
                    .setPositiveButton("OK", null)
                    .show();
        });
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}
