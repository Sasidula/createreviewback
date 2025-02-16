package com.example.createreviewback;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        // Find the login button by its ID
        Button start = findViewById(R.id.button);

        // Set an OnClickListener for the login button
        start.setOnClickListener(v -> {
            // Navigate to LoginActivity
            Intent intent = new Intent(MainActivity.this, Register.class);
            startActivity(intent);
        });


        // Find the login button by its ID
        Button loginbutton = findViewById(R.id.loginButton);

        loginbutton.setOnClickListener(v -> {
            // Navigate to LoginActivity
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        });







    }
}