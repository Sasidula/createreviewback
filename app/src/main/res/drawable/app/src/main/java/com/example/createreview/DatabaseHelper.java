package com.example.createreview;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TravelJournal.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Execute the SQL script to create tables
        db.execSQL("CREATE TABLE Locations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "location_name TEXT NOT NULL, " +
                "location_address TEXT, " +
                "location_image BLOB, " +
                "time_saved TEXT NOT NULL, " +
                "num_stars INTEGER CHECK(num_stars >= 1 AND num_stars <= 5), " +
                "reviews TEXT)");

        db.execSQL("CREATE TABLE Photos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "location_id INTEGER NOT NULL, " +
                "photo_data BLOB NOT NULL, " +
                "time_added TEXT NOT NULL, " +
                "FOREIGN KEY(location_id) REFERENCES Locations(id) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE Videos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "location_id INTEGER NOT NULL, " +
                "video_data BLOB NOT NULL, " +
                "time_added TEXT NOT NULL, " +
                "FOREIGN KEY(location_id) REFERENCES Locations(id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database version upgrades if needed
        db.execSQL("DROP TABLE IF EXISTS Videos");
        db.execSQL("DROP TABLE IF EXISTS Photos");
        db.execSQL("DROP TABLE IF EXISTS Locations");
        onCreate(db);
    }
}

