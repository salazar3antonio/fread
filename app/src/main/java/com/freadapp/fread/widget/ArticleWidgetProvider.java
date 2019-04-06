package com.freadapp.fread.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.freadapp.fread.R;

public class ArticleWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {

            // Set up the intent that starts the ArticleWidgetService, which will
            // provide the views for this collection.
            Intent intent = new Intent(context, ArticleWidgetService.class);
            // Instantiate the RemoteViews object for the app widget layout.
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);
            // Set up the RemoteViews object to use a RemoteViews adapter.
            rv.setRemoteAdapter(R.id.gv_article_widget, intent);
            // The empty view is displayed when the collection has no items.
            rv.setEmptyView(R.id.gv_article_widget, R.id.tv_empty_article_widget_view);
            // Finally update the AppWidget by passing in each appWidgetId and the RemoteViews
            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }

    }
}
