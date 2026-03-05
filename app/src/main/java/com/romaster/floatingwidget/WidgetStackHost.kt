package com.romaster.floatingwidget

import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

class WidgetStackHost : AppWidgetProvider() {
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: android.appwidget.AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    private fun updateAppWidget(
        context: Context,
        appWidgetManager: android.appwidget.AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_placeholder)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}