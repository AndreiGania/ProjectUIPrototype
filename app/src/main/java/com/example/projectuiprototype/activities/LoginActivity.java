package com.example.projectuiprototype.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectuiprototype.R;
import com.example.projectuiprototype.api.ApiClient;
import com.example.projectuiprototype.api.AuthApi;
import com.example.projectuiprototype.api.LoginRequest;
import com.example.projectuiprototype.api.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText usernameInput, passwordInput;
    Button loginButton, loginManagerButton, regButton;

    private AuthApi authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // API client
        authApi = ApiClient.getClient(this).create(AuthApi.class);


        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);

        loginButton        = findViewById(R.id.signInButton);
        loginManagerButton = findViewById(R.id.managerSignButton);
        regButton          = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(v -> login(false));
        loginManagerButton.setOnClickListener(v -> login(true));

        regButton.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void login(boolean managerLogin) {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest req = new LoginRequest(username, password);

        authApi.login(req).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(LoginActivity.this,
                            "Login failed (" + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginResponse data = response.body();
                String role = data.user != null ? data.user.role : "";

                if (managerLogin && !"manager".equals(role)) {
                    Toast.makeText(LoginActivity.this,
                            "Access denied — Manager only",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                prefs.edit()
                        .putString("token", data.token)
                        .putString("role", role)
                        .putString("userId", data.user != null ? data.user.id : "")
                        .putString("username", data.user != null ? data.user.username : "")
                        .apply();

                if ("manager".equals(role)) {
                    startActivity(new Intent(LoginActivity.this, ManagerDashboardActivity.class));
                } else {
                    startActivity(new Intent(LoginActivity.this, StaffDashboardActivity.class));
                }

                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
