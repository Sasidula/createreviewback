package com.example.createreviewback;

import android.graphics.Bitmap;

public class LocationItem {
    private int locationId;
    private String name;
    private Bitmap image;
    private double latitude;
    private double longitude;

    // Constructor
    public LocationItem(int locationId, String name, Bitmap image, double latitude, double longitude) {
        this.locationId = locationId;
        this.name = name;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters
    public int getLocationId() { return locationId; }
    public String getName() { return name; }
    public Bitmap getImage() { return image; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}

