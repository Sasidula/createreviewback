package com.example.createreviewback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity {

    private EditText fnameEditText, lnameEditText, emailEditText, passwordEditText, cpasswordEditText;
    private Button signupButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Database Helper
        dbHelper = new DatabaseHelper(this);

        // Link XML elements
        fnameEditText = findViewById(R.id.fname);
        lnameEditText = findViewById(R.id.lname);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        cpasswordEditText = findViewById(R.id.cpassword);
        signupButton = findViewById(R.id.signupButtonin);

        // Set button click listener
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = fnameEditText.getText().toString().trim();
                String lastName = lnameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String cpassword = cpasswordEditText.getText().toString().trim();

                // Validate inputs
                if (validateInputs(firstName, lastName, email, password, cpassword)) {
                    // Check if email exists
                    if (dbHelper.checkEmailExists(email)) {
                        Toast.makeText(Register.this, "Email already registered", Toast.LENGTH_SHORT).show();
                    } else {
                        // Insert user data
                        Integer isInserted = dbHelper.insertUser(firstName, lastName, email, password);
                        if (isInserted != -1) {
                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("USER_ID", isInserted);
                            editor.apply();
                            Toast.makeText(Register.this, "Registration successful, User ID: " + isInserted + ".", Toast.LENGTH_SHORT).show();
                            finish(); // Close activity or redirect
                            // Navigate to home
                            Intent intent = new Intent(Register.this, mainHome.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }




            }
        });


        Button signupButton = findViewById(R.id.txtbuttonlogin);
        // Set login button click listener
        signupButton.setOnClickListener(v -> {
            // Navigate to LoginActivity (Make sure the target activity is correct)
            Intent intent = new Intent(Register.this, Login.class); // Change LoginActivity if needed
            startActivity(intent);
        });
    }

    private boolean validateInputs(String firstName, String lastName, String email, String password, String cpassword) {
        if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(this, "Please enter your first name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(this, "Please enter your last name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(cpassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Helper method to validate email format
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        return email.matches(emailPattern);
    }






}
