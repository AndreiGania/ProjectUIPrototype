package com.example.projectuiprototype;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectuiprototype.database.DatabaseClient;
import com.example.projectuiprototype.models.User;
import com.example.projectuiprototype.dao.UserDao;

public class LoginActivity extends AppCompatActivity {

    EditText usernameInput, passwordInput;
    Button loginButton, loginManagerButton, regButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // AUTO-CREATE FIRST MANAGER ACCOUNT 🟢
        createDefaultManagerAccount();

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);

        loginButton        = findViewById(R.id.signInButton);
        loginManagerButton = findViewById(R.id.managerSignButton);
        regButton          = findViewById(R.id.registerButton);

        // Employee Login
        loginButton.setOnClickListener(v -> login(false));

        // Manager Login
        loginManagerButton.setOnClickListener(v -> login(true));

        // Go to Register
        regButton.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void login(boolean managerLogin) {

        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = DatabaseClient.getInstance(this)
                .getDatabase()
                .userDao()
                .login(username, password);

        if(user == null){
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            return;
        }

        if(managerLogin && !user.role.equals("manager")){
            Toast.makeText(this, "Access denied — Manager only", Toast.LENGTH_SHORT).show();
            return;
        }

        if(user.role.equals("manager")){
            startActivity(new Intent(this, ManagerDashboardActivity.class));
        } else {
            startActivity(new Intent(this, StaffDashboardActivity.class));
        }

        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
        finish();
    }

    // 🔥 Creates default ADMIN only ONCE
    private void createDefaultManagerAccount(){
        UserDao userDao = DatabaseClient.getInstance(this).getDatabase().userDao();

        if(userDao.getUserByUsername("admin") == null){
            User admin = new User();
            admin.name = "Administrator";
            admin.email = "admin@cafe.com";
            admin.username = "admin";
            admin.password = "admin123";
            admin.role = "manager";

            userDao.registerUser(admin); // Save in DB permanently
        }
    }
}
