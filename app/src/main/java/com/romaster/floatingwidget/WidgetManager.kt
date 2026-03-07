package com.romaster.floatingwidget

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class WidgetInfo(
    val packageName: String,
    val className: String,
    val label: String
)

class WidgetManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val SELECTED_WIDGETS_KEY = "selected_widgets"
    
    fun getSelectedWidgets(): MutableList<WidgetInfo> {
        val json = prefs.getString(SELECTED_WIDGETS_KEY, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<WidgetInfo>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            mutableListOf()
        }
    }
    
    fun saveSelectedWidgets(widgets: List<WidgetInfo>) {
        val json = gson.toJson(widgets)
        prefs.edit().putString(SELECTED_WIDGETS_KEY, json).apply()
    }
    
    // Método para mantener compatibilidad con el código existente
    fun getWidgetList(): MutableList<WidgetInfo> = getSelectedWidgets()
    
    fun saveWidgetList(widgets: List<WidgetInfo>) = saveSelectedWidgets(widgets)
    
    fun addWidget(widget: WidgetInfo) {
        val list = getSelectedWidgets().toMutableList()
        if (!list.any { it.packageName == widget.packageName && it.className == widget.className }) {
            list.add(widget)
            saveSelectedWidgets(list)
        }
    }
    
    fun removeWidget(widget: WidgetInfo) {
        val list = getSelectedWidgets().toMutableList()
        list.removeAll { it.packageName == widget.packageName && it.className == widget.className }
        saveSelectedWidgets(list)
    }
}