package com.example.createreviewback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.createreviewback.LocationModel;

import java.util.List;

public class LocationAdapter extends ArrayAdapter<LocationModel> {
    private Context context;
    private List<LocationModel> locations;

    public LocationAdapter(Context context, List<LocationModel> locations) {
        super(context, R.layout.grid_item, locations);
        this.context = context;
        this.locations = locations;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        }

        TextView locationName = convertView.findViewById(R.id.textViewLocationName);
        Button buttonGoToLocation = convertView.findViewById(R.id.button);

        LocationModel location = locations.get(position);
        locationName.setText(location.getName());

        // Handle button click to go to Location.class
        buttonGoToLocation.setOnClickListener(v -> {
            Intent intent = new Intent(context, location.class);
            intent.putExtra("location_id", location.getId());
            context.startActivity(intent);
        });

        return convertView;
    }
}
