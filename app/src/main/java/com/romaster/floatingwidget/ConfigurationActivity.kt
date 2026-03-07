package com.romaster.floatingwidget

import android.appwidget.AppWidgetManager
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
    private val selectedWidgets = mutableListOf<WidgetInfo>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        
        widgetManager = WidgetManager(this)
        
        // Cargar widgets ya seleccionados
        selectedWidgets.addAll(widgetManager.getSelectedWidgets())
        
        recyclerView = findViewById(R.id.recycler_widgets)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        adapter = WidgetSelectionAdapter(
            onWidgetSelected = { widgetInfo ->
                if (!selectedWidgets.any { it.packageName == widgetInfo.packageName && it.className == widgetInfo.className }) {
                    selectedWidgets.add(widgetInfo)
                    widgetManager.saveSelectedWidgets(selectedWidgets)
                    updateAdapterLists()
                }
            },
            onWidgetRemoved = { widgetInfo ->
                selectedWidgets.removeAll { it.packageName == widgetInfo.packageName && it.className == widgetInfo.className }
                widgetManager.saveSelectedWidgets(selectedWidgets)
                updateAdapterLists()
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
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val widgets = appWidgetManager.installedProviders
        
        val availableWidgets = widgets.map { provider ->
            WidgetInfo(
                packageName = provider.provider.packageName,
                className = provider.provider.className,
                label = provider.loadLabel(packageManager).toString()
            )
        }
        
        adapter.setAvailableWidgets(availableWidgets)
        updateAdapterLists()
    }
    
    private fun updateAdapterLists() {
        adapter.updateList(selectedWidgets)
    }
}