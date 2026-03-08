package com.romaster.floatingwidget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize

@Parcelize
data class WidgetInfo(
    val packageName: String,
    val className: String,
    val label: String,
    val previewImage: Int = 0
) : Parcelable

class WidgetManager(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    fun getAvailableWidgets(): List<WidgetInfo> {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val providers = appWidgetManager.installedProviders
        
        return providers.mapNotNull { provider ->
            try {
                context.packageManager.getReceiverInfo(provider.provider, PackageManager.GET_META_DATA)
                WidgetInfo(
                    packageName = provider.provider.packageName,
                    className = provider.provider.className,
                    label = provider.loadLabel(context.packageManager).toString(),
                    previewImage = provider.previewImage
                )
            } catch (e: Exception) {
                null
            }
        }.sortedBy { it.label }
    }
    
    fun getSelectedWidgets(): List<WidgetInfo> {
        val json = prefs.getString("selected_widgets", null) ?: return emptyList()
        val type = object : TypeToken<List<WidgetInfo>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun saveSelectedWidgets(widgets: List<WidgetInfo>) {
        val json = gson.toJson(widgets)
        prefs.edit().putString("selected_widgets", json).apply()
    }

    fun removeWidget(widget: WidgetInfo) {
        val current = getSelectedWidgets().toMutableList()
        current.removeAll { it.packageName == widget.packageName && it.className == widget.className }
        saveSelectedWidgets(current)
    }
}