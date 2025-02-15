package com.example.createreviewback;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class CalenderWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        @SuppressLint("RemoteViewLayout") RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.calender_widget);
        Intent intent = new Intent(context, CalendarWidgetService.class);
        views.setRemoteAdapter(R.id.calendar_grid, intent);

        appWidgetManager.updateAppWidget(appWidgetId, views);

    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Any functionality when the widget is first created (if needed)
    }

    @Override
    public void onDisabled(Context context) {
        // Cleanup when the widget is disabled
    }
}
