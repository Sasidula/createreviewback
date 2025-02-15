package com.example.createreviewback;

import android.content.Context;
import android.util.Log;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import java.util.Arrays;
import java.util.List;

public class PlaceDetailsFetcher {
    private static final String API_KEY = "YOUR_API_KEY";
    private PlacesClient placesClient;

    public PlaceDetailsFetcher(Context context) {
        if (!Places.isInitialized()) {
            Places.initialize(context, API_KEY);
        }
        placesClient = Places.createClient(context);
    }

    public void fetchPlaceDetails(String placeId, PlaceDetailsCallback callback) {
        List<Place.Field> fields = Arrays.asList(
                Place.Field.NAME,
                Place.Field.PHOTO_METADATAS,
                Place.Field.LAT_LNG
        );

        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, fields);
        placesClient.fetchPlace(request).addOnSuccessListener(response -> {
            Place place = response.getPlace();
            String name = place.getName();
            String imageUrl = null;
            double latitude = place.getLatLng().latitude;
            double longitude = place.getLatLng().longitude;

            if (place.getPhotoMetadatas() != null && !place.getPhotoMetadatas().isEmpty()) {
                PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
                fetchPhoto(photoMetadata, callback, name, latitude, longitude);
            } else {
                callback.onResult(name, imageUrl, latitude, longitude);
            }
        }).addOnFailureListener(e -> Log.e("GooglePlaces", "Error: " + e.getMessage()));
    }

    private void fetchPhoto(PhotoMetadata photoMetadata, PlaceDetailsCallback callback, String name, double latitude, double longitude) {
        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(500)
                .setMaxHeight(500)
                .build();

        placesClient.fetchPhoto(photoRequest).addOnSuccessListener(response -> {
            callback.onResult(name, response.getBitmap(), latitude, longitude);
        }).addOnFailureListener(e -> Log.e("GooglePlaces", "Photo error: " + e.getMessage()));
    }

    public interface PlaceDetailsCallback {
        void onResult(String name, Object image, double latitude, double longitude);
    }
}
