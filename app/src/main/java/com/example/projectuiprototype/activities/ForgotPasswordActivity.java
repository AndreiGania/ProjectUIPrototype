package com.example.projectuiprototype.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectuiprototype.R;
import com.example.projectuiprototype.api.ApiClient;
import com.example.projectuiprototype.api.AuthApi;
import com.example.projectuiprototype.api.ForgotPasswordRequest;
import com.example.projectuiprototype.api.ForgotPasswordResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button sendResetButton;
    private AuthApi authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailInput = findViewById(R.id.emailInput);
        sendResetButton = findViewById(R.id.sendResetButton);

        authApi = ApiClient.getClient(this).create(AuthApi.class);

        sendResetButton.setOnClickListener(v -> sendResetEmail());
    }

    private void sendResetEmail() {
        String email = emailInput.getText().toString().trim();

        if (email.isEmpty()) {
            emailInput.setError("Email required");
            return;
        }

        authApi.forgotPassword(new ForgotPasswordRequest(email)).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this,
                            "Request failed (" + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String msg = "If that email exists, a reset link has been sent.";
                if (response.body() != null && response.body().message != null) {
                    msg = response.body().message;
                }

                Toast.makeText(ForgotPasswordActivity.this, msg, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}