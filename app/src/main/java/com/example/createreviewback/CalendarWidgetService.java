package com.example.createreviewback;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.*;

public class CalendarWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new CalendarRemoteViewsFactory(getApplicationContext());
    }
}

class CalendarRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context context;
    private final List<String> days = new ArrayList<>();
    private Map<String, Integer> highlightedDates = new HashMap<>();

    public CalendarRemoteViewsFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        fetchCalendarData();
    }

    private long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 60000; // 1 minute interval

    @Override
    public void onDataSetChanged() {
        long currentTime = System.currentTimeMillis();

        // Only fetch and update the data if more than the interval has passed
        if (currentTime - lastUpdateTime > UPDATE_INTERVAL) {
            fetchCalendarData();
            lastUpdateTime = currentTime;

            // Notify the widget to update
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, CalenderWidget.class);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(componentName), R.id.calendar_grid);
        }
    }

    private void fetchCalendarData() {
        days.clear();
        Calendar calendar = Calendar.getInstance();
        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Fetch highlighted dates from DatabaseHelper
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        highlightedDates = dbHelper.getAllDatesWithIndices();

        for (int i = 1; i <= maxDays; i++) {
            String date = String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, i);
            days.add(date);
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.calendar_day_item);
        String date = days.get(position);
        views.setTextViewText(R.id.calendar_day_text, date.substring(8, 10)); // Show only the day part

        Log.d("CalendarWidget", "Checking: " + date); // Debugging log

        if (highlightedDates.containsKey(date)) {
            Log.d("CalendarWidget", "Highlighting: " + date); // Debugging log
            views.setInt(R.id.calendar_day_text, "setTextColor", 0xFFFF0000); // Highlight in Red
        }

        return views;
    }

    @Override
    public int getCount() {
        return days.size();
    }

    @Override
    public void onDestroy() {}

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
