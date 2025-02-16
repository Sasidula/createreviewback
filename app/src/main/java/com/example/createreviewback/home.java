package com.example.createreviewback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

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

public class home extends AppCompatActivity {

    private GridView gridView;
    //private List<LocationItem> locationList = new ArrayList<LocationItem>();

    //private LocationGridAdapter adapter;
    private PlaceDetailsFetcher placeFetcher;
    private Integer userId = 0;

    private CalendarView calendarView;
    private DatabaseHelper dbHelper;
    private Map<String, Integer> dateIndexMap;
    private int panelIndex = -1; // Initialize with a default va

    private List<LocationModel> locationList;

    private LocationAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        gridView = findViewById(R.id.gridView);
        dbHelper = new DatabaseHelper(this);

        int userId = 1;  // Change this based on logged-in user
        locationList = dbHelper.getLocationsByUserId(userId);

        adapter = new LocationAdapter(this, locationList);
        gridView.setAdapter(adapter);

        // Save ID in SharedPreferences when item is clicked
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            int selectedId = locationList.get(position).getId();
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("index", selectedId);
            editor.apply();
            Toast.makeText(home.this, "Saved index: " + selectedId, Toast.LENGTH_SHORT).show();
        });

        /*gridView = findViewById(R.id.gridView);
        adapter = new LocationGridAdapter(this, locationList);
        gridView.setAdapter(adapter);

        dbHelper = new DatabaseHelper(this);
        placeFetcher = new PlaceDetailsFetcher(this);

        fetchSavedLocations();



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the locationId of the clicked item
                int locid = locationList.get(position).getLocationId();
                // Display the locationId using a Toast (for demonstration purposes)
                Toast.makeText(home.this, "Location ID: " + locid, Toast.LENGTH_SHORT).show();
                // You can now use the locid variable as needed
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("index", locid);
                editor.apply();
            }
        });*/

        final Button button = findViewById(R.id.button4);
        if (button == null) {
            Log.e("DEBUG", "Button is null. Check your layout file.");
        } else {
            button.setOnClickListener(view -> {
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("index",5);
                editor.apply();
                Intent intent = new Intent(home.this, location.class); // Ensure class name is correct
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
                    Intent intent = new Intent(home.this, location.class);
                    startActivity(intent);
                    Log.d("MainActivity", "Clicked date: " + clickedDate + ", Index: " + panelIndex);
                } else {
                    Log.d("MainActivity", "Clicked date: " + clickedDate + ", Index: " + panelIndex);
                    // Do nothing
                }

                // Save panelIndex in SharedPreferences after it's set
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("index", panelIndex);
                editor.apply();
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

    /*private void fetchSavedLocations() {
        List<Integer> locationIds = dbHelper.getSavedLocationIds(getUserId());

        for (int placeId : locationIds) {
            placeFetcher.fetchPlaceDetails(placeId, new PlaceDetailsFetcher.PlaceDetailsCallback() {
                @Override
                public void onResult(String name, Bitmap image, double latitude, double longitude) {
                    // Add the fetched details to the locationList
                    locationList.add(new LocationItem(placeId, name, image, latitude, longitude));
                    // Notify the adapter that the data set has changed
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }*/

    public Integer getUserId(){
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", 0);
        return userId;
    }

}
