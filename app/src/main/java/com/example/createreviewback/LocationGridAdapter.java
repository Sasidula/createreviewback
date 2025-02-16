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
        return locationList.get(position).getLocationId(); // Return the locationId
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_location, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.textView = convertView.findViewById(R.id.textView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LocationItem item = locationList.get(position);
        holder.textView.setText(item.getName());
        holder.imageView.setImageBitmap(item.getImage());

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
