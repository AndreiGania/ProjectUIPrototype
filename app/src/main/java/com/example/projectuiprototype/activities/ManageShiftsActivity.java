package com.example.projectuiprototype.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
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

        etDate = findViewById(R.id.etDate);
        etPosition = findViewById(R.id.etPosition);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);

        btnAddShift = findViewById(R.id.btnAddShift);
        btnViewShifts = findViewById(R.id.btnViewShifts);

        AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getDatabase();
        shiftDao = db.shiftDao();

        etDate.setOnClickListener(v -> showDatePicker());
        etStartTime.setOnClickListener(v -> showTimePicker(etStartTime));
        etEndTime.setOnClickListener(v -> showTimePicker(etEndTime));

        btnAddShift.setOnClickListener(v -> addShift());
        btnViewShifts.setOnClickListener(v -> showShiftsDialog());
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String iso = String.format(Locale.US, "%04d-%02d-%02d",
                            year, (month + 1), dayOfMonth);
                    etDate.setText(iso);
                },
                y, m, d
        );

        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void showTimePicker(TextInputEditText target) {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> target.setText(formatTo12Hour(selectedHour, selectedMinute)),
                hour, min,
                false
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

        Shift shift = new Shift();
        shift.day = date + " • " + position;
        shift.time = start + " - " + end;
        shift.userId = 1;

        shiftDao.addShift(shift);

        Toast.makeText(this, "Shift added", Toast.LENGTH_SHORT).show();

        etDate.setText("");
        etPosition.setText("");
        etStartTime.setText("");
        etEndTime.setText("");
    }


    private void showShiftsDialog() {
        List<Shift> shifts = shiftDao.getAllShifts();

        if (shifts == null || shifts.isEmpty()) {
            Toast.makeText(this, "No shifts saved yet", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_shifts_list, null);
        ListView listView = dialogView.findViewById(R.id.listShifts);

        ShiftListAdapter adapter = new ShiftListAdapter(shifts, shift -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete shift?")
                    .setMessage("Delete this shift?\n\n" + shift.day + "\n" + shift.time)
                    .setPositiveButton("Delete", (d, which) -> {
                        shiftDao.deleteShift(shift);
                        Toast.makeText(this, "Shift deleted", Toast.LENGTH_SHORT).show();
                        showShiftsDialog(); // refresh list after delete
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        listView.setAdapter(adapter);

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Close", null)
                .show();
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}
