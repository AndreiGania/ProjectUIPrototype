package com.example.projectuiprototype.activities;

import android.os.Bundle;
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


        fullName   = findViewById(R.id.fullName);
        emailAdd   = findViewById(R.id.emailAdd);
        userInput  = findViewById(R.id.UserInput);
        passInput  = findViewById(R.id.PassInput);
        repeatPass = findViewById(R.id.repeatPass);

        registerBtn = findViewById(R.id.btn_registeracc);
        registerBtn.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name     = fullName.getText().toString().trim();
        String email    = emailAdd.getText().toString().trim();
        String username = userInput.getText().toString().trim();
        String password = passInput.getText().toString().trim();
        String confirm  = repeatPass.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

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
                if (!response.isSuccessful()) {
                    // 409 = user exists, 400 = missing fields, etc.
                    Toast.makeText(RegisterActivity.this,
                            "Register failed (" + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(RegisterActivity.this,
                        "Account created successfully!",
                        Toast.LENGTH_SHORT).show();

                finish();
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
