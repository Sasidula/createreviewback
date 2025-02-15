package com.example.createreviewback;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends BaseAdapter {
    private Context context;
    private List<Review> reviews;

    public ReviewAdapter(Context context, ArrayList<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grid_item_review, parent, false);
        } else {
            view = convertView;
        }

        Review review = reviews.get(position);

        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        TextView reviewText = view.findViewById(R.id.reviewText);

        ratingBar.setRating(review.getStars());
        reviewText.setText(review.getText());

        return view;
    }
}
