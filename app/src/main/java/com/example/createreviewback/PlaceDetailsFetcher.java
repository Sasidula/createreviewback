package com.example.createreviewback;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
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

    public void fetchPlaceDetails(int placeId, final PlaceDetailsCallback callback) {
        String placeIdString = String.valueOf(placeId);
        List<Place.Field> fields = Arrays.asList(
                Place.Field.NAME,
                Place.Field.PHOTO_METADATAS,
                Place.Field.LAT_LNG
        );

        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeIdString, fields);
        placesClient.fetchPlace(request).addOnSuccessListener(response -> {
            Place place = response.getPlace();
            String name = place.getName();
            double latitude = place.getLatLng().latitude;
            double longitude = place.getLatLng().longitude;

            if (place.getPhotoMetadatas() != null && !place.getPhotoMetadatas().isEmpty()) {
                PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
                fetchPhoto(photoMetadata, new PhotoFetchCallback() {
                    @Override
                    public void onPhotoFetched(Bitmap image) {
                        callback.onResult(name, image, latitude, longitude);
                    }
                });
            } else {
                callback.onResult(name, null, latitude, longitude);
            }
        }).addOnFailureListener(e -> Log.e("PlaceDetailsFetcher", "Place not found: " + e.getMessage()));
    }

    private void fetchPhoto(PhotoMetadata photoMetadata, final PhotoFetchCallback callback) {
        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(500) // Optional: Specify the maximum width of the image
                .setMaxHeight(500) // Optional: Specify the maximum height of the image
                .build();

        placesClient.fetchPhoto(photoRequest).addOnSuccessListener(fetchPhotoResponse -> {
            Bitmap bitmap = fetchPhotoResponse.getBitmap();
            callback.onPhotoFetched(bitmap);
        }).addOnFailureListener(e -> Log.e("PlaceDetailsFetcher", "Photo not found: " + e.getMessage()));
    }

    public interface PlaceDetailsCallback {
        void onResult(String name, Bitmap image, double latitude, double longitude);
    }

    private interface PhotoFetchCallback {
        void onPhotoFetched(Bitmap image);
    }
}
