package com.example.createreviewback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class usereditact extends AppCompatActivity {
    private EditText fname, lname, email, password, cpassword;
    private Button editDetails, editPassword;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_account);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);

        ImageView map = findViewById(R.id.btmap);
        map.setOnClickListener(v -> {
            Intent intent = new Intent(usereditact.this, GoogleMaps.class);
            startActivity(intent);
        });

        ImageView home = findViewById(R.id.bthome);
        home.setOnClickListener(v -> {
            Intent intent = new Intent(usereditact.this, mainHome.class);
            startActivity(intent);
        });

        ImageView search = findViewById(R.id.btsearch);
        search.setOnClickListener(v -> {
            Intent intent = new Intent(usereditact.this, home.class);
            startActivity(intent);
        });

        ImageView account = findViewById(R.id.btacc);
        account.setOnClickListener(v -> {
            Intent intent = new Intent(usereditact.this, usereditact.class);
            startActivity(intent);
        });

        dbHelper = new DatabaseHelper(this);

        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        cpassword = findViewById(R.id.cpassword);
        editDetails = findViewById(R.id.editdtails);
        editPassword = findViewById(R.id.editpassword);

        // Button to update First Name, Last Name, and Email
        editDetails.setOnClickListener(v -> {
            String firstName = fname.getText().toString().trim();
            String lastName = lname.getText().toString().trim();
            String emailText = email.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || emailText.isEmpty()) {
                Toast.makeText(usereditact.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean updated = dbHelper.updateUserDetails(userId, firstName, lastName, emailText);
            if (updated) {
                Toast.makeText(usereditact.this, "User details updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(usereditact.this, "Update failed!", Toast.LENGTH_SHORT).show();
            }
        });

        // Button to update Password
        editPassword.setOnClickListener(v -> {
            String newPassword = password.getText().toString().trim();
            String confirmPassword = cpassword.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(usereditact.this, "Please enter a password!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(usereditact.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean updated = dbHelper.updatePassword(userId, newPassword);
            if (updated) {
                Toast.makeText(usereditact.this, "Password updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(usereditact.this, "Update failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Integer setUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("USER_ID", -1); // Default value is -1 if not found
    }
}


