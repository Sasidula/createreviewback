package com.example.createreviewback;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.createreviewback.databinding.ActivityGoogleMapsBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GoogleMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText searchEditText;
    private ImageView searchIcon;
    private ActivityGoogleMapsBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final float DEFAULT_ZOOM = 15f; // Zoom level
    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGoogleMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageView home = findViewById(R.id.home_icon);
        home.setOnClickListener(v -> {
            Intent intent = new Intent(GoogleMaps.this, mainHome.class);
            startActivity(intent);
        });

        ImageView search = findViewById(R.id.search_button);
        search.setOnClickListener(v -> {
            Intent intent = new Intent(GoogleMaps.this, home.class);
            startActivity(intent);
        });

        ImageView viewmap = findViewById(R.id.location_icon);
        viewmap.setOnClickListener(v -> {
            Intent intent = new Intent(GoogleMaps.this, GoogleMaps.class);
            startActivity(intent);
        });

        ImageView profile = findViewById(R.id.profile_icon);
        profile.setOnClickListener(v -> {
            Intent intent = new Intent(GoogleMaps.this, usereditact.class);
            startActivity(intent);
        });


        searchEditText = findViewById(R.id.search_edit_text);
        searchIcon = findViewById(R.id.search_icon);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBo2bVN3FMTA9xkigRqns734vH-A2UaWGg");
        }
        placesClient = Places.createClient(this);

        searchIcon.setOnClickListener(v -> {
            String query = searchEditText.getText().toString();
            if (!query.isEmpty()) {
                searchLocation(query);
            } else {
                Toast.makeText(GoogleMaps.this, "Enter a location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchLocation(String location) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            return; // Stop execution until permission is granted
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(location, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                // 🔹 Get Place ID dynamically
                getPlaceIdAndShowBottomSheet(location, latLng);

            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e("GoogleMaps", "Geocoder error: " + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied! Enable location access in settings.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setupAutoComplete() {
        searchEditText.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .build(this);

            startActivityForResult(intent, 100);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);

            if (place.getLatLng() != null) {
                LatLng latLng = place.getLatLng();

                // Move camera to the selected place
                mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }

        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("MissingPermission")
    private void getPlaceIdAndShowBottomSheet(String locationName, LatLng latLng) {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        // Create a request to get places related to the location name
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(fields);

        placesClient.findCurrentPlace(request).addOnSuccessListener(response -> {
            if (!response.getPlaceLikelihoods().isEmpty()) {
                Place place = response.getPlaceLikelihoods().get(0).getPlace();
                String placeId = place.getId();

                // ✅ Ensure placeId is not null before proceeding
                if (placeId != null) {
                    showBottomSheet(locationName, placeId);
                } else {
                    Log.e("GoogleMaps", "Place ID is null, showing bottom sheet without details.");
                    showBottomSheet(locationName, null);
                }
            } else {
                Log.e("GoogleMaps", "No places found for: " + locationName);
                showBottomSheet(locationName, null);
            }
        }).addOnFailureListener(e -> {
            Log.e("GoogleMaps", "Failed to fetch Place ID: " + e.getMessage());
            showBottomSheet(locationName, null);
        });
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getDeviceLocation();
        } else {
            requestLocationPermission();
        }

        mMap.setOnMarkerClickListener(marker -> {
            showBottomSheet(marker.getTitle(), "PLACE_ID_HERE");
            return false;
        });
    }

    private void showBottomSheet(String locationName, @Nullable String placeId) {
        if (placeId == null) {
            Log.e("GoogleMaps", "Place ID is null. Showing bottom sheet without image or rating.");
            LocationBottomSheet bottomSheet = new LocationBottomSheet(locationName.toUpperCase(), placeId, null, 0.0);
            bottomSheet.show(getSupportFragmentManager(), "LocationBottomSheet");
            return;
        }

        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.PHOTO_METADATAS, Place.Field.RATING);
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener(response -> {
            Place place = response.getPlace();
            double rating = (place.getRating() != null) ? place.getRating() : 0.0;

            // Fetching Image if available
            if (place.getPhotoMetadatas() != null && !place.getPhotoMetadatas().isEmpty()) {
                PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata).build();

                placesClient.fetchPhoto(photoRequest).addOnSuccessListener(fetchPhotoResponse -> {
                    Bitmap placeImage = fetchPhotoResponse.getBitmap();
                    LocationBottomSheet bottomSheet = new LocationBottomSheet(locationName.toUpperCase(), placeId, placeImage, rating);
                    bottomSheet.show(getSupportFragmentManager(), "LocationBottomSheet");
                }).addOnFailureListener(e -> {
                    Log.e("GoogleMaps", "Photo fetch failed: " + e.getMessage());
                    LocationBottomSheet bottomSheet = new LocationBottomSheet(locationName.toUpperCase(), placeId, null, rating);
                    bottomSheet.show(getSupportFragmentManager(), "LocationBottomSheet");
                });

            } else {
                Log.e("GoogleMaps", "No photos available for this place.");
                LocationBottomSheet bottomSheet = new LocationBottomSheet(locationName.toUpperCase(), placeId,null, rating);
                bottomSheet.show(getSupportFragmentManager(), "LocationBottomSheet");
            }

        }).addOnFailureListener(e -> {
            Log.e("GoogleMaps", "Fetching place details failed: " + e.getMessage());
            LocationBottomSheet bottomSheet = new LocationBottomSheet(locationName.toUpperCase(), placeId , null, 0.0);
            bottomSheet.show(getSupportFragmentManager(), "LocationBottomSheet");
        });
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    private void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(currentLatLng).title("You are here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM));
            } else {
                Log.e("GoogleMaps", "Location is null");
            }
        });
    }
}
