package com.example.createreviewback;

import android.graphics.Bitmap;

public class LocationItem {
    private String name;
    private Bitmap image; // Assuming image is a Bitmap
    private double latitude;
    private double longitude;

    // Constructor
    public LocationItem(String name, Bitmap image, double latitude, double longitude) {
        this.name = name;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters
    public String getName() { return name; }
    public Bitmap getImage() { return image; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}

