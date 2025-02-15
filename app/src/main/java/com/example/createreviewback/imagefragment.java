package com.example.createreviewback;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class imagefragment extends Fragment {
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private GridView gridView;
    private ImageAdapter imageAdapter;
    private ArrayList<Integer> imageIds;
    private ArrayList<Bitmap> imageList;
    private SQLiteDatabase database;

    public imagefragment() {
        // Required empty public constructor
    }

    public static imagefragment newInstance() {
        return new imagefragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.imagecode, container, false);

        gridView = view.findViewById(R.id.gridView);
        imageList = new ArrayList<>();
        imageIds = new ArrayList<>();
        imageAdapter = new ImageAdapter(requireContext(), imageList, imageIds);
        gridView.setAdapter(imageAdapter);

        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        database = dbHelper.getWritableDatabase();
        loadImagesFromDatabase();

        Button btnAddPhoto = view.findViewById(R.id.btnAddPhoto);
        btnAddPhoto.setOnClickListener(v -> pickImageFromGallery());

        Button btnCapturePhoto = view.findViewById(R.id.btnCapturePhoto);
        btnCapturePhoto.setOnClickListener(v -> captureImage());

        return view;
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void captureImage() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            Bitmap bitmap = null;
            if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri imageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");
            }

            if (bitmap != null) {
                saveImageToDatabase(bitmap);
                imageList.add(bitmap);
                imageAdapter.notifyDataSetChanged();
            }
        }
    }

    private void saveImageToDatabase(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        DatabaseHelper imageHelper = new DatabaseHelper(requireContext());
        imageHelper.insertImage(byteArray);
    }

    private void loadImagesFromDatabase() {
        Cursor cursor = database.query("images", null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") byte[] byteArray = cursor.getBlob(cursor.getColumnIndex("image"));
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                imageList.add(bitmap);
                imageIds.add(id);
            } while (cursor.moveToNext());
            cursor.close();
        }
        imageAdapter.notifyDataSetChanged();
    }

    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Bitmap> images;
        private ArrayList<Integer> imageIds;

        public ImageAdapter(Context context, ArrayList<Bitmap> images, ArrayList<Integer> imageIds) {
            this.context = context;
            this.images = images;
            this.imageIds = imageIds;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object getItem(int position) {
            return images.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.grid_item_layout, parent, false);
            } else {
                view = convertView;
            }

            ImageView imageView = view.findViewById(R.id.imageView);
            ImageButton btnDelete = view.findViewById(R.id.btnDelete);

            Bitmap bitmap = images.get(position);
            imageView.setImageBitmap(bitmap);

            btnDelete.setOnClickListener(v -> {
                int imageId = imageIds.get(position);
                deleteImageFromDatabase(imageId);

                requireActivity().runOnUiThread(() -> {
                    images.remove(position);
                    imageIds.remove(position);
                    notifyDataSetChanged();
                });
            });

            return view;
        }

        private void deleteImageFromDatabase(int imageId) {
            database.delete("images", "id = ?", new String[]{String.valueOf(imageId)});
        }
    }
}

