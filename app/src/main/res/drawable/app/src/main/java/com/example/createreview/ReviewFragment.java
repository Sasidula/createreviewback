package com.example.createreview;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ReviewFragment extends Fragment {

    public ReviewFragment() {
        // Required empty public constructor
    }

    // Optional: If arguments are needed in the future, use this method
    public static ReviewFragment newInstance() {
        return new ReviewFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        return view;
    }

    private ImageView[] stars;
    private int currentRating = 0; // Holds the current star rating (0 to 5)

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
    }

    /**
     * Sets the star rating and updates the UI.
     *
     * @param rating The new rating (1 to 5)
     */
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

    /**
     * Retrieves the current rating.
     *
     * @return The current rating (0 to 5)
     */
    public int getCurrentRating() {
        return currentRating;
    }

}

