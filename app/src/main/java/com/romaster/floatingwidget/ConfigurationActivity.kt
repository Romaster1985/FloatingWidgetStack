package com.romaster.floatingwidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ConfigurationActivity : AppCompatActivity() {
    
    private lateinit var widgetManager: WidgetManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WidgetSelectionAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        
        widgetManager = WidgetManager(this)
        
        recyclerView = findViewById(R.id.recycler_widgets)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        adapter = WidgetSelectionAdapter(
            onWidgetSelected = { widgetInfo ->
                widgetManager.addWidget(widgetInfo)
                adapter.updateList(widgetManager.getWidgetList())
            },
            onWidgetRemoved = { widgetInfo ->
                widgetManager.removeWidget(widgetInfo)
                adapter.updateList(widgetManager.getWidgetList())
            }
        )
        
        recyclerView.adapter = adapter
        
        findViewById<Button>(R.id.btn_save).setOnClickListener {
            Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show()
            finish()
        }
        
        // Cargar widgets disponibles
        loadAvailableWidgets()
    }
    
    private fun loadAvailableWidgets() {
        val widgetManager = AppWidgetManager.getInstance(this)
        val widgets = widgetManager.installedProviders
        
        val availableWidgets = widgets.map { provider ->
            WidgetInfo(
                packageName = provider.provider.packageName,
                className = provider.provider.className,
                label = provider.loadLabel(packageManager).toString()
            )
        }
        
        adapter.setAvailableWidgets(availableWidgets)
        adapter.updateList(widgetManager.getWidgetList())
    }
}