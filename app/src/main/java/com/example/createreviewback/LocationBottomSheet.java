package com.example.createreviewback;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LocationBottomSheet extends BottomSheetDialogFragment {
    private String locationName;
    private String placeId;
    private Bitmap placeImage;
    private double rating;
    private String uderid;

    public LocationBottomSheet(String locationName, String placeId , Bitmap placeImage, double rating) {
        this.locationName = locationName;
        this.placeId = placeId;
        this.placeImage = placeImage;
        this.rating = rating;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_bottom_sheet, container, false);

        TextView locationTextView = view.findViewById(R.id.location_name);
        ImageView placeImageView = view.findViewById(R.id.location_image);
        RatingBar ratingBar = view.findViewById(R.id.location_rating);
        LinearLayout btnSaved = view.findViewById(R.id.btn_saved);

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


        locationTextView.setText(locationName);
        ratingBar.setRating((float) rating); // Ensure `rating` is a float

        if (placeImage != null) {
            placeImageView.setImageBitmap(placeImage);
        } else {
            placeImageView.setImageResource(R.drawable.default_location_image); // Default image
        }

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1); // Default value is -1 if not found

        if (userId != -1) {
            Toast.makeText(requireContext(), "User ID: " + userId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "No User ID found", Toast.LENGTH_SHORT).show();
        }


        // Save location when "Save" button is clicked
        btnSaved.setOnClickListener(v -> {
            boolean isSaved = dbHelper.saveLocation(placeId, locationName, currentDate, userId);
            if (isSaved) {
                Toast.makeText(getContext(), "Location Saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

}
