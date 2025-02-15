package com.example.createreviewback;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class reviewfragment extends Fragment {

    private ImageView[] stars;
    private EditText reviewText;
    private int currentRating = 0; // Holds the current star rating (0 to 5)
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reviewcode, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        reviewText = view.findViewById(R.id.review_text);
        Button btnSubmitReview = view.findViewById(R.id.add_review_button);

        // Initialize star ImageViews
        stars = new ImageView[]{
                view.findViewById(R.id.star_1),
                view.findViewById(R.id.star_2),
                view.findViewById(R.id.star_3),
                view.findViewById(R.id.star_4),
                view.findViewById(R.id.star_5)
        };

        // Set click listeners for each star
        for (int i = 0; i < stars.length; i++) {
            int index = i; // final variable for use in the lambda
            stars[i].setOnClickListener(v -> setRating(index + 1));
        }

        btnSubmitReview.setOnClickListener(v -> {
            String noteText = reviewText.getText().toString().trim();
            if (!noteText.isEmpty()) {
                int stars = getCurrentRating();
                // Save or update the review in the database
                saveReviewtoDatabase(stars, noteText);
                reviewText.setText(""); // Clear the input field
            }
        });
        return view;
    }

    private void setRating(int rating) {
        currentRating = rating;

        // Update stars
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                // Filled stars
                stars[i].setImageResource(R.drawable.filled);
            } else {
                // Empty stars
                stars[i].setImageResource(R.drawable.empty);
            }
        }
    }
    public int getCurrentRating() {
        return currentRating;
    }

    private void saveReviewtoDatabase(int stars, String noteText) {
        dbHelper.insertOrUpdateReview(stars, noteText);
    }
}


