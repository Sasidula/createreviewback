package com.example.createreviewback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class mainHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_home);

        Integer userId = setUserId();

        if (userId != -1) {
            Toast.makeText(this, "User ID: " + userId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No User ID found", Toast.LENGTH_SHORT).show();
        }

        ImageView map = findViewById(R.id.btmap);
        map.setOnClickListener(v -> {
            Intent intent = new Intent(mainHome.this, GoogleMaps.class);
            startActivity(intent);
        });

        ImageView home = findViewById(R.id.bthome);
        home.setOnClickListener(v -> {
            Intent intent = new Intent(mainHome.this, mainHome.class);
            startActivity(intent);
        });

        ImageView search = findViewById(R.id.btsearch);
        search.setOnClickListener(v -> {
            Intent intent = new Intent(mainHome.this, home.class);
            startActivity(intent);
        });

        ImageView account = findViewById(R.id.btacc);
        account.setOnClickListener(v -> {
            Intent intent = new Intent(mainHome.this, usereditact.class);
            startActivity(intent);
        });


    }
    public Integer setUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("USER_ID", -1); // Default value is -1 if not found
    }

}