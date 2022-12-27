package com.crypto.prices.view.widget.watchlist

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.crypto.prices.R
import com.crypto.prices.utils.MyAnalytics

class WatchlistWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Get all ids
        val thisWidget = ComponentName(
            context,
            WatchlistWidgetProvider::class.java
        )
        val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
        for (widgetId in allWidgetIds) {
            val remoteViews = RemoteViews(
                context.packageName,
                R.layout.widget_layout
            )
            remoteViews.setTextViewText(R.id.textView_app_desc, " - WatchList")
            remoteViews.setRemoteAdapter(
                R.id.widget_list,
                Intent(context, WatchlistWidgetService::class.java)
            )
            appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.widget_list)

            // Register an onClickListener
            /*Intent intent = new Intent(context, CollectionWidgetProvider.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.imageViewRefresh, pendingIntent);*/
            appWidgetManager.updateAppWidget(
                widgetId,
                remoteViews
            )
        }
        // commenting since widget service is not working in background
        scheduleUpdates(context)
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        //Toast.makeText(context, "onEnabled called", Toast.LENGTH_LONG).show()
        MyAnalytics.trackScreenViews(javaClass.simpleName, "onEnabled")
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        cancelUpdates(context)
        MyAnalytics.trackScreenViews(javaClass.simpleName, "onDisabled")
        super.onDisabled(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // reschedule update alarm so it does not include ID of currently removed widget
        // commenting since widget service is not working in background
        scheduleUpdates(context)
        super.onDeleted(context, appWidgetIds)
    }

    private fun getActiveWidgetIds(context: Context): IntArray {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, this::class.java)

        // return ID of all active widgets within this AppWidgetProvider
        return appWidgetManager.getAppWidgetIds(componentName)
    }

    private fun scheduleUpdates(context: Context) {
        val activeWidgetIds = getActiveWidgetIds(context)

        if (activeWidgetIds.isNotEmpty()) {

            val nextUpdate = System.currentTimeMillis() + WIDGET_UPDATE_INTERVAL
            val pendingIntent = getUpdatePendingIntent(context)

            context.alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                nextUpdate, // alarm time in millis since 1970-01-01 UTC
                pendingIntent
            )
        }
    }

    private fun cancelUpdates(context: Context) {
        context.alarmManager.cancel(getUpdatePendingIntent(context))
    }

    private fun getUpdatePendingIntent(context: Context): PendingIntent {
        val widgetClass = this::class.java
        val widgetIds = getActiveWidgetIds(context)
        val updateIntent = Intent(context, widgetClass)
            .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
        val requestCode = widgetClass.name.hashCode()
        val flags = PendingIntent.FLAG_CANCEL_CURRENT or
                PendingIntent.FLAG_IMMUTABLE

        return PendingIntent.getBroadcast(context, requestCode, updateIntent, flags)
    }

    private val Context.alarmManager: AlarmManager
        get() = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        private const val ACTION_CLICK = "ACTION_CLICK"
        private val WIDGET_UPDATE_INTERVAL: Long = 3600000
    }
}