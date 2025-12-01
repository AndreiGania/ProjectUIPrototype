package com.example.projectuiprototype.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectuiprototype.R;
import com.example.projectuiprototype.database.DatabaseClient;
import com.example.projectuiprototype.models.User;

public class RegisterActivity extends AppCompatActivity {

    EditText fullName, emailAdd, userInput, passInput, repeatPass;
    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Link fields to XML IDs
        fullName   = findViewById(R.id.fullName);
        emailAdd   = findViewById(R.id.emailAdd);
        userInput  = findViewById(R.id.UserInput);
        passInput  = findViewById(R.id.PassInput);
        repeatPass = findViewById(R.id.repeatPass);

        registerBtn = findViewById(R.id.btn_registeracc);

        registerBtn.setOnClickListener(v -> registerUser());
    }

    private void registerUser(){

        String name     = fullName.getText().toString().trim();
        String email    = emailAdd.getText().toString().trim();
        String username = userInput.getText().toString().trim();
        String password = passInput.getText().toString().trim();
        String confirm  = repeatPass.getText().toString().trim();

        if(name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(confirm)){
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User();
        user.name = name;
        user.email = email;
        user.username = username;
        user.password = password;
        user.role = "employee";

        DatabaseClient.getInstance(this)
                .getDatabase()
                .userDao()
                .registerUser(user);

        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
