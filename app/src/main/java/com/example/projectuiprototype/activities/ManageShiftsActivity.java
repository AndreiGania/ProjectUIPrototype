package com.example.projectuiprototype.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
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
import com.example.projectuiprototype.api.ApiClient;
import com.example.projectuiprototype.api.CreateShiftRequest;
import com.example.projectuiprototype.api.ShiftApi;
import com.example.projectuiprototype.api.ShiftDto;
import com.example.projectuiprototype.models.Shift;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageShiftsActivity extends AppCompatActivity {

    private TextInputEditText etDate;
    private TextInputEditText etPosition;
    private TextInputEditText etStartTime;
    private TextInputEditText etEndTime;

    private MaterialButton btnAddShift;
    private MaterialButton btnViewShifts;

    private ShiftApi shiftApi;

    // Loaded from SharedPreferences (set in LoginActivity)
    private String employeeId;
    private String employeeUsername;

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

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        employeeId = prefs.getString("userId", null);

        // Choose ONE:
        // employeeUsername = prefs.getString("username", null); // shows "test"
        employeeUsername = prefs.getString("username", null);

        if (employeeId == null || employeeId.isEmpty() || employeeUsername == null || employeeUsername.isEmpty()) {
            Toast.makeText(this, "Missing user info. Please login again.", Toast.LENGTH_LONG).show();
        }

        etDate = findViewById(R.id.etDate);
        etPosition = findViewById(R.id.etPosition);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);

        btnAddShift = findViewById(R.id.btnAddShift);
        btnViewShifts = findViewById(R.id.btnViewShifts);

        // Retrofit API (token auto-attached by ApiClient interceptor)
        shiftApi = ApiClient.getClient(getApplicationContext()).create(ShiftApi.class);

        etDate.setOnClickListener(v -> showDatePicker());
        etStartTime.setOnClickListener(v -> showTimePicker(etStartTime));
        etEndTime.setOnClickListener(v -> showTimePicker(etEndTime));

        btnAddShift.setOnClickListener(v -> addShiftToServer());
        btnViewShifts.setOnClickListener(v -> fetchAndShowShifts());
    }

    // ---------------- Pickers ----------------
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

    private String getText(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    /**
     * Converts ("2026-02-16", "9:00 AM") -> "2026-02-16T14:00:00.000Z" (UTC)
     */
    private String toIsoUtc(String dateYYYYMMDD, String time12h) throws Exception {
        String combined = dateYYYYMMDD + " " + time12h;

        SimpleDateFormat inFmt = new SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.US);
        inFmt.setLenient(false);

        Date parsed = inFmt.parse(combined);

        SimpleDateFormat outFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        outFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        return outFmt.format(parsed);
    }

    // ---------------- API: POST shift ----------------
    private void addShiftToServer() {
        if (employeeId == null || employeeId.isEmpty() || employeeUsername == null || employeeUsername.isEmpty()) {
            Toast.makeText(this, "Please login again (missing user info).", Toast.LENGTH_LONG).show();
            return;
        }

        String date = getText(etDate);
        String position = getText(etPosition);
        String start12 = getText(etStartTime);
        String end12 = getText(etEndTime);

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
        if (start12.isEmpty()) {
            etStartTime.setError("Start time required");
            etStartTime.requestFocus();
            return;
        }
        if (end12.isEmpty()) {
            etEndTime.setError("End time required");
            etEndTime.requestFocus();
            return;
        }

        String startIso, endIso;
        try {
            startIso = toIsoUtc(date, start12);
            endIso = toIsoUtc(date, end12);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid date/time format", Toast.LENGTH_SHORT).show();
            return;
        }

        CreateShiftRequest req = new CreateShiftRequest(
                employeeId,
                employeeUsername,
                startIso,
                endIso
        );

        shiftApi.createShift(req).enqueue(new Callback<ShiftDto>() {
            @Override
            public void onResponse(Call<ShiftDto> call, Response<ShiftDto> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(ManageShiftsActivity.this,
                            "Create failed: " + response.code(),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(ManageShiftsActivity.this, "Shift saved", Toast.LENGTH_SHORT).show();

                etDate.setText("");
                etPosition.setText("");
                etStartTime.setText("");
                etEndTime.setText("");
            }

            @Override
            public void onFailure(Call<ShiftDto> call, Throwable t) {
                Toast.makeText(ManageShiftsActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // ---------------- API: GET shifts + show dialog using your adapter ----------------
    private void fetchAndShowShifts() {
        shiftApi.getShifts().enqueue(new Callback<List<ShiftDto>>() {
            @Override
            public void onResponse(Call<List<ShiftDto>> call, Response<List<ShiftDto>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(ManageShiftsActivity.this,
                            "Load failed: " + response.code(),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                List<ShiftDto> serverShifts = response.body();
                if (serverShifts == null || serverShifts.isEmpty()) {
                    Toast.makeText(ManageShiftsActivity.this, "No shifts on server yet", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Map ShiftDto -> your local Shift model (day/time) for adapter reuse
                List<Shift> listForAdapter = new ArrayList<>();
                for (ShiftDto s : serverShifts) {
                    Shift local = new Shift();

                    String niceDate = (s.start != null) ? formatDateNice(s.start) : "Unknown date";
                    String niceStart = (s.start != null) ? formatTimeNice(s.start) : "?";
                    String niceEnd = (s.end != null) ? formatTimeNice(s.end) : "?";

                    local.day = s.employeeUsername + " • " + niceDate;
                    local.time = niceStart + " - " + niceEnd;


                    local.userId = 1;

                    listForAdapter.add(local);
                }

                showShiftsDialog(listForAdapter);
            }

            @Override
            public void onFailure(Call<List<ShiftDto>> call, Throwable t) {
                Toast.makeText(ManageShiftsActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private String formatDateNice(String iso) {
        try {
            SimpleDateFormat inFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            inFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date d = inFmt.parse(iso);

            SimpleDateFormat outFmt = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            outFmt.setTimeZone(TimeZone.getDefault()); // local time
            return outFmt.format(d);
        } catch (Exception e) {
            return iso;
        }
    }

    private String formatTimeNice(String iso) {
        try {
            SimpleDateFormat inFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            inFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date d = inFmt.parse(iso);

            SimpleDateFormat outFmt = new SimpleDateFormat("h:mm a", Locale.US);
            outFmt.setTimeZone(TimeZone.getDefault()); // local time
            return outFmt.format(d);
        } catch (Exception e) {
            return iso;
        }
    }
    private void showShiftsDialog(List<Shift> shifts) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_shifts_list, null);
        ListView listView = dialogView.findViewById(R.id.listShifts);

        final ShiftListAdapter[] adapter = new ShiftListAdapter[1];

        adapter[0] = new ShiftListAdapter(shifts, shift -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete shift?")
                    .setMessage("Delete this shift?\n\n" + shift.day + "\n" + shift.time)
                    .setPositiveButton("Delete", (d, which) -> {
                        shifts.remove(shift);
                        adapter[0].notifyDataSetChanged();
                        Toast.makeText(this, "Removed from list", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        listView.setAdapter(adapter[0]);

        new AlertDialog.Builder(this)
                .setTitle("Shifts (Server)")
                .setView(dialogView)
                .setPositiveButton("Close", null)
                .show();
    }
}