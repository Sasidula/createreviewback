package com.example.createreviewback;

public class Review {
    private float stars;
    private String text;

    public Review(float stars, String text) {
        this.stars = stars;
        this.text = text;
    }

    public float getStars() {
        return stars;
    }

    public String getText() {
        return text;
    }
}
