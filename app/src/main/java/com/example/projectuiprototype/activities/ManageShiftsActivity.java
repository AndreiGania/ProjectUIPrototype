package com.example.projectuiprototype.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ManageShiftsActivity extends AppCompatActivity {

    private TextInputEditText etDate;
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Views
        etDate = findViewById(R.id.etDate);
        etPosition = findViewById(R.id.etPosition);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);

        btnAddShift = findViewById(R.id.btnAddShift);
        btnViewShifts = findViewById(R.id.btnViewShifts);

        // DB
        AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getDatabase();
        shiftDao = db.shiftDao();

        // Pickers
        etDate.setOnClickListener(v -> showDatePicker());
        etStartTime.setOnClickListener(v -> showTimePicker(etStartTime));
        etEndTime.setOnClickListener(v -> showTimePicker(etEndTime));

        // Add shift
        btnAddShift.setOnClickListener(v -> addShift());

        // View shifts
        btnViewShifts.setOnClickListener(v -> viewAllShifts());
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // ISO date: YYYY-MM-DD (best practice for databases)
                    String iso = String.format(Locale.US, "%04d-%02d-%02d",
                            year, (month + 1), dayOfMonth);
                    etDate.setText(iso);
                },
                y, m, d
        );

        // Professional: prevent past scheduling (optional, but good)
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        dialog.show();
    }

    private void showTimePicker(TextInputEditText target) {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    // 12-hour display like "9:00 AM"
                    String time = formatTo12Hour(selectedHour, selectedMinute);
                    target.setText(time);
                },
                hour, min,
                false // false = 12-hour clock
        );

        dialog.show();
    }

    private String formatTo12Hour(int hour24, int minute) {
        int hour12 = hour24 % 12;
        if (hour12 == 0) hour12 = 12;

        String amPm = (hour24 < 12) ? "AM" : "PM";
        return String.format(Locale.US, "%d:%02d %s", hour12, minute, amPm);
    }

    private void addShift() {
        String date = getText(etDate);
        String position = getText(etPosition);
        String start = getText(etStartTime);
        String end = getText(etEndTime);

        if (date.isEmpty()) {
            etDate.setError("Date required");
            etDate.requestFocus();
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }
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

        // Store start-end in your existing 'time' field
        String timeRange = start + " - " + end;

        Shift shift = new Shift();

        // Keep DB unchanged:
        // day field stores "DATE • POSITION"
        shift.day = date + " • " + position;

        // time field stores "Start - End"
        shift.time = timeRange;

        // Your placeholder user
        shift.userId = 1;

        shiftDao.addShift(shift);

        Toast.makeText(this, "Shift added", Toast.LENGTH_SHORT).show();

        etDate.setText("");
        etPosition.setText("");
        etStartTime.setText("");
        etEndTime.setText("");
    }

    private void viewAllShifts() {
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
                    .append(s.day)   // "DATE • POSITION"
                    .append("\n   ")
                    .append(s.time)  // "Start - End"
                    .append("\n\n");
        }

        new AlertDialog.Builder(this)
                .setTitle("All Shifts")
                .setMessage(sb.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}
