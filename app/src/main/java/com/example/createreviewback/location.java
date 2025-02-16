package com.example.createreviewback;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class location extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);

        ImageView map = findViewById(R.id.btmap);
        map.setOnClickListener(v -> {
            Intent intent = new Intent(location.this, GoogleMaps.class);
            startActivity(intent);
        });

        ImageView home = findViewById(R.id.bthome);
        home.setOnClickListener(v -> {
            Intent intent = new Intent(location.this, mainHome.class);
            startActivity(intent);
        });

        ImageView search = findViewById(R.id.btsearch);
        search.setOnClickListener(v -> {
            Intent intent = new Intent(location.this, home.class);
            startActivity(intent);
        });

        ImageView account = findViewById(R.id.btacc);
        account.setOnClickListener(v -> {
            Intent intent = new Intent(location.this, usereditact.class);
            startActivity(intent);
        });


        // Initialize button
        Button button = findViewById(R.id.button_explore);
        button.setOnClickListener(v -> openGoogleMaps());

        Button showReviewsButton = findViewById(R.id.button_reviews);
        showReviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReviewsPopup(v);
            }
        });

        Button btnCheckVisit = findViewById(R.id.button_reminder);


        ImageView planStatusImageView = findViewById(R.id.planStatusImageView);

        // Check if the loc_index exists in the plan table
        dbHelper = new DatabaseHelper(this);
        if (dbHelper.isLocIndexInPlanTable(getCurrentLocationIndex())) {

            btnCheckVisit.setText("edit");
            btnCheckVisit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPlanDetailsDialog();
                }
            });

            planStatusImageView.setImageResource(R.drawable.gray_cc);// Set to image2
            planStatusImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //**********//pass to location table
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Change format as needed
                    String currentDate = sdf.format(new Date());
                    int locIndex = getCurrentLocationIndex();
                    //passtolocation table updatedatebyindex(locIndex,currentDate);

                    // Delete the row with the specified loc_index
                    String msg = dbHelper.deletePlanRow(getCurrentLocationIndex());
                    // Revert to the default image
                    planStatusImageView.setImageResource(R.drawable.gold_cc);
                    // Remove the click listener
                    planStatusImageView.setOnClickListener(null);

                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(location.this, msg, duration);
                    toast.show();
                }
            });
        } else {
            btnCheckVisit.setText("+reminder");
            btnCheckVisit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPlanDetailsDialog();
                }
            });


            planStatusImageView.setImageResource(R.drawable.gold_cc); // Set to default image
            planStatusImageView.setOnClickListener(null); // No action needed

        }

        // Set up fragment switching buttons
        findViewById(R.id.button_image).setOnClickListener(view -> replaceFragment(new imagefragment()));
        findViewById(R.id.button_video).setOnClickListener(view -> replaceFragment(new videofragment()));
        findViewById(R.id.button_note).setOnClickListener(view -> replaceFragment(new notefragment()));
        findViewById(R.id.button_review).setOnClickListener(view -> replaceFragment(new reviewfragment()));
    }

    private void openGoogleMaps() {
        // Example Coordinates: Empire State Building, NYC
        double latitude = 40.748817;
        double longitude = -73.985428;

        // Create URI for Google Maps
        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude);

        // Create Intent
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        // Check if Google Maps is installed
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Open in a web browser if Google Maps is not installed
            Uri webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude);
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
            startActivity(webIntent);
        }
    }

    // Generic method to replace fragments
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showReviewsPopup(View anchorView) {
        // Inflate the popup layout
        View popupView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.popup_reviews, null);

        // Initialize the PopupWindow
        final PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // Set a transparent background to ensure proper behavior
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Set up the GridView
        GridView gridView = popupView.findViewById(R.id.gridViewReviews);
        dbHelper = new DatabaseHelper(anchorView.getContext());
        ArrayList<Review> reviews = dbHelper.getAllReviews();
        ReviewAdapter adapter = new ReviewAdapter(anchorView.getContext(), reviews);
        gridView.setAdapter(adapter);

        // Apply dim to the background
        applyDim((ViewGroup) anchorView.getRootView(), 0.5f);

        // Set a dismiss listener to clear the dim when the popup is dismissed
        popupWindow.setOnDismissListener(() -> clearDim((ViewGroup) anchorView.getRootView()));

        // Show the PopupWindow
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
    }

    private void applyDim(ViewGroup parent, float dimAmount) {
        ColorDrawable dim = new ColorDrawable(Color.BLACK);
        dim.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        dim.setAlpha((int) (255 * dimAmount));
        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.add(dim);
    }

    private void clearDim(ViewGroup parent) {
        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.clear();
    }


    private void showPlanDetailsDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View promptsView = inflater.inflate(R.layout.dialog_plan_details, null);

        final EditText etDate = promptsView.findViewById(R.id.etDate);
        final EditText etStartTime = promptsView.findViewById(R.id.etStartTime);
        final EditText etEndTime = promptsView.findViewById(R.id.etEndTime);

        Button btnDelete = promptsView.findViewById(R.id.btnDelete);

        if (dbHelper.isLocIndexInPlanTable(getCurrentLocationIndex())) {
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String msg = dbHelper.deletePlanRow(getCurrentLocationIndex());
                }
            });
        }
        else{
            btnDelete.setVisibility(View.GONE);
        }

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(etDate);
            }
        });

        etStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(etStartTime);
            }
        });

        etEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(etEndTime);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(promptsView)
                .setCancelable(false)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String date = etDate.getText().toString();
                        String startTime = etStartTime.getText().toString();
                        String endTime = etEndTime.getText().toString();

                        if (validateInputs(date, startTime, endTime)) {
                            savePlanDetails(date, startTime, endTime);
                        } else {
                            Toast.makeText(location.this, "Please enter valid details.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void showDatePickerDialog(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                        editText.setText(selectedDate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        editText.setText(selectedTime);
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }



    private boolean validateInputs(String date, String startTime, String endTime) {
        // Implement your validation logic here
        // For example, check if the strings are not empty and match the required format
        return !date.isEmpty() && !startTime.isEmpty() && !endTime.isEmpty();
    }

    private void savePlanDetails(String date, String startTime, String endTime) {
        // Implement the logic to save the plan details in the database
        // For example, using your DatabaseHelper class:
        dbHelper = new DatabaseHelper(this);
        int locIndex = getCurrentLocationIndex(); // Implement this method to get the current location index
        dbHelper.insertOrUpdatePlan(locIndex, date, startTime, endTime);
        // Refresh the current activity
        recreate();
    }

    private int getCurrentLocationIndex() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("index", 0);
    }

}



    /*private void showVisitStatusDialog() {
        showPlanDetailsDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Visit Status")
                .setMessage("Have you visited this location?")
                .setPositiveButton("Visited", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User confirmed they visited the location
                        // Update the status table accordingly
                        updateVisitStatus(1); // Assuming 1 represents 'visited'
                    }
                })
                .setNegativeButton("Not Visited", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User has not visited the location
                        showPlanDetailsDialog();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showPlanDetailsDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View promptsView = inflater.inflate(R.layout.dialog_plan_details, null);

        final EditText etDate = promptsView.findViewById(R.id.etDate);
        final EditText etStartTime = promptsView.findViewById(R.id.etStartTime);
        final EditText etEndTime = promptsView.findViewById(R.id.etEndTime);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(promptsView)
                .setCancelable(false)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Retrieve user input and save to the plan table
                        String date = etDate.getText().toString();
                        String startTime = etStartTime.getText().toString();
                        String endTime = etEndTime.getText().toString();

                        // Validate inputs (e.g., check for empty strings, correct format)
                        if (validateInputs(date, startTime, endTime)) {
                            // Insert these details into the plan table
                            savePlanDetails(date, startTime, endTime);
                        } else {
                            Toast.makeText(MainActivity.this, "Please enter valid details.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateVisitStatus(int status) {
        // Implement the logic to update the visit status in the database
        // For example, using your DatabaseHelper class:
        dbHelper = new DatabaseHelper(this);
        int locIndex = getCurrentLocationIndex(); // Implement this method to get the current location index
        dbHelper.insertOrUpdateStatus(locIndex, status);
    }*/

