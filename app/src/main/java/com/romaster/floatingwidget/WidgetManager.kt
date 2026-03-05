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
    
    fun getWidgetList(): MutableList<WidgetInfo> {
        val json = prefs.getString("widgets", null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<WidgetInfo>>() {}.type
        return gson.fromJson(json, type)
    }
    
    fun saveWidgetList(widgets: List<WidgetInfo>) {
        val json = gson.toJson(widgets)
        prefs.edit().putString("widgets", json).apply()
    }
    
    fun addWidget(widget: WidgetInfo) {
        val list = getWidgetList().toMutableList()
        if (!list.any { it.packageName == widget.packageName && it.className == widget.className }) {
            list.add(widget)
            saveWidgetList(list)
        }
    }
    
    fun removeWidget(widget: WidgetInfo) {
        val list = getWidgetList().toMutableList()
        list.removeAll { it.packageName == widget.packageName && it.className == widget.className }
        saveWidgetList(list)
    }
}