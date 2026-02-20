package com.example.projectuiprototype.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projectuiprototype.R;
import com.example.projectuiprototype.api.ApiClient;
import com.example.projectuiprototype.api.ShiftApi;
import com.example.projectuiprototype.api.ShiftDto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyScheduleActivity extends AppCompatActivity {

    private TextView tvScheduleContent;
    private Button btnRequestChange;

    private ShiftApi shiftApi;

    private String myUserId;      // Mongo user.id string from login response
    private String myUsername;    // username from login response

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

        // ✅ Pull logged-in user from SharedPreferences (saved in LoginActivity)
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        myUserId = prefs.getString("userId", null);
        myUsername = prefs.getString("username", null);

        if (myUserId == null || myUserId.isEmpty()) {
            Toast.makeText(this, "Missing user info. Please login again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // ✅ Retrofit API (token auto-attached by ApiClient interceptor)
        shiftApi = ApiClient.getClient(getApplicationContext()).create(ShiftApi.class);

        loadScheduleFromServer();

        // Keep your existing request-change dialog
        btnRequestChange.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Shift Change Request")
                    .setMessage("Your shift change request has been sent to the manager.")
                    .setPositiveButton("OK", null)
                    .show();
        });
    }

    private void loadScheduleFromServer() {
        tvScheduleContent.setText("Loading schedule...");

        shiftApi.getShifts().enqueue(new Callback<List<ShiftDto>>() {
            @Override
            public void onResponse(Call<List<ShiftDto>> call, Response<List<ShiftDto>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    tvScheduleContent.setText("Failed to load schedule (" + response.code() + ")");
                    return;
                }

                List<ShiftDto> all = response.body();

                StringBuilder sb = new StringBuilder();
                int count = 0;

                for (ShiftDto s : all) {
                    if (s == null) continue;

                    // Only show shifts for THIS logged-in user
                    if (s.employeeId != null && s.employeeId.equals(myUserId)) {
                        String niceDate = (s.start != null) ? formatDateNice(s.start) : "Unknown date";
                        String niceStart = (s.start != null) ? formatTimeNice(s.start) : "?";
                        String niceEnd = (s.end != null) ? formatTimeNice(s.end) : "?";

                        String who = (myUsername != null && !myUsername.isEmpty())
                                ? myUsername
                                : (s.employeeUsername != null ? s.employeeUsername : "Me");

                        sb.append(who)
                                .append(" • ")
                                .append(niceDate)
                                .append(" — ")
                                .append(niceStart)
                                .append(" - ")
                                .append(niceEnd)
                                .append("\n\n");

                        count++;
                    }
                }

                if (count == 0) {
                    tvScheduleContent.setText("You have no shifts scheduled yet.");
                } else {
                    tvScheduleContent.setText(sb.toString().trim());
                }
            }

            @Override
            public void onFailure(Call<List<ShiftDto>> call, Throwable t) {
                tvScheduleContent.setText("Network error: " + t.getMessage());
            }
        });
    }

    private String formatDateNice(String iso) {
        try {
            SimpleDateFormat inFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            inFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date d = inFmt.parse(iso);

            SimpleDateFormat outFmt = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            outFmt.setTimeZone(TimeZone.getDefault());
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
            outFmt.setTimeZone(TimeZone.getDefault());
            return outFmt.format(d);
        } catch (Exception e) {
            return iso;
        }
    }
}