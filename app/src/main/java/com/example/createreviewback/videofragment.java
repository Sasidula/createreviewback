package com.example.createreviewback;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaDataSource;
import android.media.MediaMetadataRetriever;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class videofragment extends Fragment {
    private static final int REQUEST_VIDEO_PICK = 1;
    private static final int REQUEST_VIDEO_CAPTURE = 2;
    private GridView gridView;
    private VideoAdapter videoAdapter;
    private ArrayList<Integer> videoIds;
    private ArrayList<Bitmap> videoThumbnails;
    private SQLiteDatabase database;

    public videofragment() {
        // Required empty public constructor
    }

    public static videofragment newInstance() {
        return new videofragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.videocode, container, false);

        gridView = view.findViewById(R.id.gridView);
        videoThumbnails = new ArrayList<>();
        videoIds = new ArrayList<>();
        videoAdapter = new VideoAdapter(requireContext(), videoThumbnails, videoIds);
        gridView.setAdapter(videoAdapter);

        // Load videos from the database
        try {
            loadVideosFromDatabase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Button btnAddVideo = view.findViewById(R.id.btnAddVideo);
        btnAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_VIDEO_PICK);
            }
        });

        Button btnCaptureVideo = view.findViewById(R.id.btnCaptureVideo);
        btnCaptureVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_VIDEO_CAPTURE);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri videoUri = data.getData();
            try {
                byte[] videoBytes = getBytesFromUri(videoUri);
                // Save the video to the database
                saveVideoToDatabase(videoBytes);
                // Add the video thumbnail to the list and notify the adapter
                Bitmap thumbnail = getVideoThumbnail(videoUri);
                videoThumbnails.add(thumbnail);
                videoAdapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Failed to process video", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private byte[] getBytesFromUri(Uri uri) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return outputStream.toByteArray();
    }


    private void saveVideoToDatabase(byte[] videoBytes) {
        DatabaseHelper videoHelper = new DatabaseHelper(requireContext());
        videoHelper.insertVideo(videoBytes);
    }

    private void loadVideosFromDatabase() throws IOException {
        DatabaseHelper videoHelper = new DatabaseHelper(requireContext());
        SQLiteDatabase database = videoHelper.getWritableDatabase();
        Cursor cursor = database.query("Videos", null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") byte[] videoBytes = cursor.getBlob(cursor.getColumnIndex("video"));
                Bitmap thumbnail = getVideoThumbnail(videoBytes);
                videoThumbnails.add(thumbnail);
                videoIds.add(id); // Store the video ID
            } while (cursor.moveToNext());
            cursor.close();
        }
        videoAdapter.notifyDataSetChanged();
    }

    private Bitmap getVideoThumbnail(Uri uri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(requireContext(), uri);
        Bitmap thumbnail = retriever.getFrameAtTime(0);
        retriever.release();
        return thumbnail;
    }

    private Bitmap getVideoThumbnail(byte[] videoBytes) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(new MediaDataSource() {
                @Override
                public int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
                    if (position >= videoBytes.length) {
                        return -1; // End of data
                    }
                    int length = size;
                    if (position + size > videoBytes.length) {
                        length = (int) (videoBytes.length - position);
                    }
                    System.arraycopy(videoBytes, (int) position, buffer, offset, length);
                    return length;
                }

                @Override
                public long getSize() throws IOException {
                    return videoBytes.length;
                }

                @Override
                public void close() throws IOException {
                    // No resources to close
                }
            });
            return retriever.getFrameAtTime(0); // Retrieve the first frame
        } finally {
            retriever.release();
        }
    }

    public class VideoAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Bitmap> thumbnails;
        private ArrayList<Integer> videoIds; // List to store video IDs from the database

        public VideoAdapter(Context context, ArrayList<Bitmap> thumbnails, ArrayList<Integer> videoIds) {
            this.context = context;
            this.thumbnails = thumbnails;
            this.videoIds = videoIds;
        }

        @Override
        public int getCount() {
            return thumbnails.size();
        }

        @Override
        public Object getItem(int position) {
            return thumbnails.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                // Inflate a new view if convertView is null
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.grid_item_layout, parent, false);
            } else {
                // Reuse the existing view
                view = convertView;
            }

            // Retrieve the ImageView from the layout
            ImageView imageView = view.findViewById(R.id.imageView);
            ImageButton btnDelete = view.findViewById(R.id.btnDelete);

            // Set the thumbnail for the ImageView
            Bitmap thumbnail = thumbnails.get(position);
            imageView.setImageBitmap(thumbnail);

            // Set click listener to play video
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int videoId = videoIds.get(position);
                    playVideo(videoId);
                }
            });

            // Set click listener to delete video
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Remove the video from the database
                    int videoId = videoIds.get(position);
                    deleteVideoFromDatabase(videoId);

                    // Remove the video from the list and notify the adapter
                    thumbnails.remove(position);
                    videoIds.remove(position);
                    notifyDataSetChanged();
                }
            });

            return view;
        }

        private void playVideo(int videoId) {
            // Retrieve the video bytes from the database
            byte[] videoBytes = getVideoFromDatabase(videoId);
            if (videoBytes != null) {
                try {
                    // Create a temporary file to play the video
                    File tempFile = File.createTempFile("video", "mp4", context.getCacheDir());
                    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                        fos.write(videoBytes);
                    }

                    // Get the URI of the temporary file
                    Uri videoUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", tempFile);

                    // Play the video using an intent
                    Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
                    intent.setDataAndType(videoUri, "video/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Failed to play video", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Video not found", Toast.LENGTH_SHORT).show();
            }
        }

        private byte[] getVideoFromDatabase(int videoId) {
            Cursor cursor = database.query("Videos", new String[]{"video"}, "id = ?", new String[]{String.valueOf(videoId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                @SuppressLint("Range") byte[] videoBytes = cursor.getBlob(cursor.getColumnIndex("video"));
                cursor.close();
                return videoBytes;
            }
            return null;
        }

        private void deleteVideoFromDatabase(int videoId) {
            database.delete("Videos", "id = ?", new String[]{String.valueOf(videoId)});
        }
    }
}
