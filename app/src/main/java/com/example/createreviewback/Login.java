package com.example.createreviewback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    private EditText email, password;
    private Button btnLogin;

    private Button resgister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        resgister =findViewById(R.id.txtbuttonreg);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        dbHelper = new DatabaseHelper(this);

        resgister.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputEmail = email.getText().toString().trim();
                String inputPassword = password.getText().toString().trim();

                if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
                    Toast.makeText(Login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    if (dbHelper.checkEmailExists(inputEmail)) {
                        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                                "SELECT id FROM user_table WHERE email=? AND password=?",
                                new String[]{inputEmail, inputPassword}
                        );

                        if (cursor.moveToFirst()) {  // Ensure there is a result before accessing data
                            int userIdIndex = cursor.getColumnIndex("id");
                            if (userIdIndex != -1) {  // Ensure column exists
                                int userId = cursor.getInt(userIdIndex);
                                cursor.close();

                                // Store in SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("USER_ID", userId);
                                editor.apply();

                                Toast.makeText(Login.this, "Login Successful. User ID: " + userId, Toast.LENGTH_SHORT).show();

                                // Navigate to Home
                                Intent intent = new Intent(Login.this, mainHome.class);
                                intent.putExtra("USER_ID", userId);
                                startActivity(intent);
                            } else {
                                Toast.makeText(Login.this, "Login Error: Column 'id' not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Login.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                        }
                        cursor.close();
                    } else {
                        Toast.makeText(Login.this, "Email not registered", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });




    }
}




