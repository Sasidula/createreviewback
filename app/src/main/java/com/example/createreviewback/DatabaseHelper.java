package com.example.createreviewback;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MediaDB";
    private static final int DATABASE_VERSION = 1;

    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_INDEX = "loc_index";

    private static final String TABLE_IMAGES = "Images";
    private static final String TABLE_VIDEOS = "Videos";
    public static final String TABLE_NOTES = "Notes";
    public static final String TABLE_REVIEWS = "Reviews";
    private static final String TABLE_STATUS = "state";
    private static final String TABLE_PLAN = "Fplan";
    private static final String TABLE_Locations = "Locations";

    public static final String COLUMN_ID = "id";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_VIDEO = "video";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_STAR_REVIEW = "star_review";
    public static final String COLUMN_Comment_RATING = "Comment_rating";

    private static final String COLUMN_STATUS = "status";

    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_START_TIME = "start_time";
    private static final String COLUMN_END_TIME = "end_time";

    private static final String COLUMN_LOCATION_NAME = "location_name";
    private static final String COLUMN_LOCATION_ID = "location_id";
    private static final String COLUMN_SAVED_DATE = "save_date";

    private SharedPreferences sharedPreferences;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    }

    private int getCurrentIndex() {
        return sharedPreferences.getInt("index", 0);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createImageTable = "CREATE TABLE " + TABLE_IMAGES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_IMAGE + " BLOB, " +
                COLUMN_INDEX + " INTEGER)";

        String createVideoTable = "CREATE TABLE " + TABLE_VIDEOS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_VIDEO + " BLOB, " +
                COLUMN_INDEX + " INTEGER)";

        String createNotesTable = "CREATE TABLE " + TABLE_NOTES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOTE + " TEXT, " +
                COLUMN_INDEX + " INTEGER)"; // Added space before INTEGER

        String createReviewTable = "CREATE TABLE " + TABLE_REVIEWS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_STAR_REVIEW + " INTEGER, " +
                COLUMN_Comment_RATING + " TEXT, " +
                COLUMN_INDEX + " INTEGER UNIQUE)";

        String createStatusTable = "CREATE TABLE " + TABLE_STATUS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_INDEX + " INTEGER UNIQUE, "
                + COLUMN_STATUS + " INTEGER)";

        String createPlanTable = "CREATE TABLE " + TABLE_PLAN + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_INDEX + " INTEGER UNIQUE, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_START_TIME + " TEXT, "
                + COLUMN_END_TIME + " TEXT)";

        String createTable = "CREATE TABLE " + TABLE_Locations + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LOCATION_ID + " TEXT, " +
                COLUMN_LOCATION_NAME + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_USER_ID + " INTEGER)";

        db.execSQL(createImageTable);
        db.execSQL(createVideoTable);
        db.execSQL(createNotesTable);
        db.execSQL(createReviewTable);
        db.execSQL(createStatusTable);
        db.execSQL(createPlanTable);
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATUS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Locations);
        onCreate(db);
    }

    public void insertImage(byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE, image);
        values.put(COLUMN_INDEX, getCurrentIndex()); // Assuming getCurrentIndex() is defined
        db.insert(TABLE_IMAGES, null, values);
    }

    public Bitmap getImage(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_IMAGES, new String[]{COLUMN_IMAGE},
                COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            byte[] imageBytes = cursor.getBlob(0);
            cursor.close();
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        }
        return null;
    }

    public void insertVideo(byte[] video) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_VIDEO, video);
        values.put(COLUMN_INDEX, getCurrentIndex());
        db.insert(TABLE_VIDEOS, null, values);
    }

    public Bitmap getvideo(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_VIDEOS, new String[]{COLUMN_VIDEO},
                COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            byte[] videoBytes = cursor.getBlob(0);
            cursor.close();
            return BitmapFactory.decodeByteArray(videoBytes, 0, videoBytes.length);
        }
        return null;
    }

    public void insertNote(String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE, note);
        values.put(COLUMN_INDEX, getCurrentIndex());
        db.insert(TABLE_NOTES, null, values);
    }

    public String getNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, new String[]{COLUMN_NOTE},
                COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String note = cursor.getString(0);
            cursor.close();
            return note;
        }
        return null;
    }

    public void insertOrUpdateReview(int starReview, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STAR_REVIEW, starReview);
        values.put(COLUMN_Comment_RATING, comment);
        values.put(COLUMN_INDEX, getCurrentIndex());

        // Use INSERT with ON CONFLICT clause to perform an upsert
        db.insertWithOnConflict(TABLE_REVIEWS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public String getReview(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_REVIEWS, new String[]{COLUMN_STAR_REVIEW, COLUMN_Comment_RATING},
                COLUMN_INDEX + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String note = cursor.getString(0);
            cursor.close();
            return note;
        }
        return null;
    }

    public void insertOrUpdateStatus(int locIndex, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INDEX, locIndex);
        values.put(COLUMN_STATUS, status);
        db.insertWithOnConflict(TABLE_STATUS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public ArrayList<Review> getAllReviews() {
        ArrayList<Review> reviews = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Reviews", null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") float stars = cursor.getFloat(cursor.getColumnIndex("star_review"));
                @SuppressLint("Range") String text = cursor.getString(cursor.getColumnIndex("Comment_rating"));
                reviews.add(new Review(stars, text));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return reviews;
    }

    public void insertOrUpdatePlan(int locIndex, String date, String startTime, String endTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INDEX, locIndex);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_START_TIME, startTime);
        values.put(COLUMN_END_TIME, endTime);
        db.insertWithOnConflict(TABLE_PLAN, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    boolean isLocIndexInPlanTable(int locIndex) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {DatabaseHelper.COLUMN_INDEX};
        String selection = DatabaseHelper.COLUMN_INDEX + " = ?";
        String[] selectionArgs = {String.valueOf(locIndex)};
        String limit = "1";

        Cursor cursor = db.query(DatabaseHelper.TABLE_PLAN, columns, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    String deletePlanRow(int locIndex) {
        String msg;
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = DatabaseHelper.COLUMN_INDEX + " = ?";
        String[] whereArgs = {String.valueOf(locIndex)};
        int deletedRows = db.delete(DatabaseHelper.TABLE_PLAN, whereClause, whereArgs);

        if (deletedRows > 0) {
            msg ="Plan entry deleted.";
        } else {
            msg = "No entry found to delete.";
        }
        return msg;
    }

    @SuppressLint("Range")
    public String getDateByIndex(int locIndex) {
        SQLiteDatabase db = this.getReadableDatabase();
        String date = null;
        String query = "SELECT " + COLUMN_DATE + " FROM " + TABLE_PLAN + " WHERE " + COLUMN_INDEX + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(locIndex)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
            }
            cursor.close();
        }
        db.close();
        return date;
    }

    public Map<String, Integer> getAllDatesWithIndices() {
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, Integer> dateIndexMap = new HashMap<>();

        String[] columns = {COLUMN_INDEX, COLUMN_DATE};
        Cursor cursor = db.query(TABLE_PLAN, columns, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int index = cursor.getInt(cursor.getColumnIndex(COLUMN_INDEX));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                dateIndexMap.put(date, index);
            }
            cursor.close();
        }
        db.close();
        Log.d("CalendarWidget", "Stored Dates: " + dateIndexMap.toString());
        return dateIndexMap;
    }

    public void setReminderForPlans(Context context) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Get current time
        long currentTime = System.currentTimeMillis();

        // Query the plan table to get date and start time
        String query = "SELECT " + COLUMN_DATE + ", " + COLUMN_START_TIME + " FROM " + TABLE_PLAN;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                @SuppressLint("Range") String startTime = cursor.getString(cursor.getColumnIndex(COLUMN_START_TIME));

                // Convert date and time to timestamp (you need to handle this conversion)
                long eventTime = convertDateTimeToTimestamp(date, startTime);

                if (eventTime > currentTime) {
                    scheduleNotification(context, eventTime);
                }

            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private long convertDateTimeToTimestamp(String date, String startTime) {
        // Combine date and time and parse it into a timestamp (use SimpleDateFormat)
        String dateTime = date + " " + startTime;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Log.d("Reminder", "Date: " + date + ", Start Time: " + startTime);
        try {
            Date parsedDate = format.parse(dateTime);
            return parsedDate != null ? parsedDate.getTime() : 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNotification(Context context, long eventTime) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Set the alarm to trigger at the event time
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, eventTime, pendingIntent);
    }

    public List<String> getSavedLocationIds(Integer userId) {
        List<String> locationIds = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT location_id FROM " + TABLE_Locations + " WHERE user_id=?", new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                locationIds.add(cursor.getString(0)); // Get location_id
            } while (cursor.moveToNext());
        }
        cursor.close();
        return locationIds;
    }
}
