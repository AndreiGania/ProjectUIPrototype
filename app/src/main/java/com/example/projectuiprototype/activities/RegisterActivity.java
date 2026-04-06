package com.example.projectuiprototype.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectuiprototype.R;
import com.example.projectuiprototype.api.ApiClient;
import com.example.projectuiprototype.api.AuthApi;
import com.example.projectuiprototype.api.RegisterRequest;
import com.example.projectuiprototype.api.RegisterResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText fullName, emailAdd, userInput, passInput, repeatPass;
    Button registerBtn;

    private AuthApi authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authApi = ApiClient.getClient(this).create(AuthApi.class);

        fullName = findViewById(R.id.fullName);
        emailAdd = findViewById(R.id.emailAdd);
        userInput = findViewById(R.id.UserInput);
        passInput = findViewById(R.id.PassInput);
        repeatPass = findViewById(R.id.repeatPass);

        registerBtn = findViewById(R.id.btn_registeracc);
        registerBtn.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = fullName.getText().toString().trim();
        String email = emailAdd.getText().toString().trim();
        String username = userInput.getText().toString().trim();
        String password = passInput.getText().toString().trim();
        String confirm = repeatPass.getText().toString().trim();

        fullName.setError(null);
        emailAdd.setError(null);
        userInput.setError(null);
        passInput.setError(null);
        repeatPass.setError(null);

        if (name.isEmpty()) {
            fullName.setError("Enter your full name");
            fullName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            emailAdd.setError("Enter your email");
            emailAdd.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailAdd.setError("Enter a valid email address");
            emailAdd.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            userInput.setError("Enter a username");
            userInput.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passInput.setError("Enter a password");
            passInput.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passInput.setError("Password must be at least 6 characters");
            passInput.requestFocus();
            return;
        }

        if (confirm.isEmpty()) {
            repeatPass.setError("Confirm your password");
            repeatPass.requestFocus();
            return;
        }

        if (!password.equals(confirm)) {
            repeatPass.setError("Passwords do not match");
            repeatPass.requestFocus();
            return;
        }

        registerBtn.setEnabled(false);
        registerBtn.setText("Creating account...");

        RegisterRequest req = new RegisterRequest(
                name,
                email,
                username,
                password,
                "staff"
        );

        authApi.register(req).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                registerBtn.setEnabled(true);
                registerBtn.setText("Register");

                if (!response.isSuccessful()) {
                    String errorMessage;

                    if (response.code() == 409) {
                        errorMessage = "Email or username is already registered.";
                    } else if (response.code() == 400) {
                        errorMessage = "Please enter a valid email and complete all fields.";
                    } else if (response.code() == 500) {
                        errorMessage = "Server error. Please try again.";
                    } else {
                        errorMessage = "Registration failed. Please try again.";
                    }

                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(
                        RegisterActivity.this,
                        "Account created! Please verify your email before logging in.",
                        Toast.LENGTH_LONG
                ).show();

                finish();
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                registerBtn.setEnabled(true);
                registerBtn.setText("Register");

                Toast.makeText(
                        RegisterActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}