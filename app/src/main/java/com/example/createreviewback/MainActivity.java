package com.example.createreviewback;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private DatabaseHelper dbHelper;
    private Map<String, Integer> dateIndexMap;
    private int panelIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = findViewById(R.id.button4);
        if (button == null) {
            Log.e("DEBUG", "Button is null. Check your layout file.");
        } else {
            button.setOnClickListener(view -> {
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("index",5);
                editor.apply();
                Intent intent = new Intent(MainActivity.this, location.class); // Ensure class name is correct
                startActivity(intent);
            });
        }

        calendarView = findViewById(R.id.calendarView);
        dbHelper = new DatabaseHelper(this);

        // Retrieve dates and indices from the database
        dateIndexMap = dbHelper.getAllDatesWithIndices();

        // Highlight dates in the calendar
        List<EventDay> events = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (String dateString : dateIndexMap.keySet()) {
            try {
                Date date = sdf.parse(dateString);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                events.add(new EventDay(calendar, R.drawable.sample_icon)); // Use your custom icon
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        calendarView.setEvents(events);

        // Set a listener for date clicks
        calendarView.setOnDayClickListener(eventDay -> {
            Calendar clickedDayCalendar = eventDay.getCalendar();
            String clickedDate = sdf.format(clickedDayCalendar.getTime());

            if (dateIndexMap.containsKey(clickedDate)) {
                panelIndex = dateIndexMap.get(clickedDate);
                if (panelIndex != 0) {
                    Intent intent = new Intent(MainActivity.this, location.class);
                    startActivity(intent);
                    Log.d("MainActivity", "Clicked date: " + clickedDate + ", Index: " + panelIndex);
                } else {
                    Log.d("MainActivity", "Clicked date: " + clickedDate + ", Index: " + panelIndex);
                    //do nothing
                }

                // Perform actions with panelIndex as needed
            } else {
                // Handle the case where the clicked date is not in the database
            }
        });

        // Save data in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("index",panelIndex);
        editor.apply();

    }

}
