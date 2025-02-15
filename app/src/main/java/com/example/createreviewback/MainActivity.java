package com.example.createreviewback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.GridView;

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

    private GridView gridView;
    private List<LocationItem> locationList = new ArrayList<LocationItem>();

    private LocationGridAdapter adapter;
    private PlaceDetailsFetcher placeFetcher;
    private Integer userId;

    private CalendarView calendarView;
    private DatabaseHelper dbHelper;
    private Map<String, Integer> dateIndexMap;
    private int panelIndex;

    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.gridView);
        adapter = new LocationGridAdapter(this, locationList);
        gridView.setAdapter(adapter);

        dbHelper = new DatabaseHelper(this);
        placeFetcher = new PlaceDetailsFetcher(this);
        editor.putInt("user_id",1);
        editor.apply();

        fetchSavedLocations();

        final Button button = findViewById(R.id.button4);
        if (button == null) {
            Log.e("DEBUG", "Button is null. Check your layout file.");
        } else {
            button.setOnClickListener(view -> {
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
        editor.putInt("index",panelIndex);
        editor.apply();

    }

    private void fetchSavedLocations() {
        List<String> locationIds = dbHelper.getSavedLocationIds(userId);

        for (String placeId : locationIds) {
            placeFetcher.fetchPlaceDetails(placeId, (name, image, latitude, longitude) -> {
                locationList.add(new LocationItem(name, image, latitude, longitude));
                adapter.notifyDataSetChanged();
            });
        }
    }

    private int getCurrentUserId() {
        return sharedPreferences.getInt("user_id", 0);
    }

}
