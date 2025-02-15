package com.example.createreviewback;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class LocationGridAdapter extends BaseAdapter {
    private Context context;
    private List<LocationItem> locationList;

    public LocationGridAdapter(Context context, List<LocationItem> locationList) {
        this.context = context;
        this.locationList = locationList;
    }

    @Override
    public int getCount() {
        return locationList.size();
    }

    @Override
    public Object getItem(int position) {
        return locationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_location, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView textView = convertView.findViewById(R.id.textView);

        LocationItem item = locationList.get(position);
        textView.setText(item.getName());

        if (item.getImage() instanceof Bitmap) {
            imageView.setImageBitmap((Bitmap) item.getImage());
        }

        return convertView;
    }
}
