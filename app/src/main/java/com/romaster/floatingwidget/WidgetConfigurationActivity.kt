package com.romaster.floatingwidget

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WidgetConfigurationActivity : AppCompatActivity() {

    private lateinit var widgetManager: WidgetManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WidgetSelectionAdapter
    private val selectedWidgets = mutableListOf<WidgetInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)

        widgetManager = WidgetManager(this)
        selectedWidgets.addAll(widgetManager.getSelectedWidgets())

        recyclerView = findViewById(R.id.recycler_widgets)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = WidgetSelectionAdapter(
            onWidgetSelected = { widget ->
                if (!selectedWidgets.any { it.packageName == widget.packageName && it.className == widget.className }) {
                    selectedWidgets.add(widget)
                    widgetManager.saveSelectedWidgets(selectedWidgets)
                    updateAdapter()
                    Toast.makeText(this, "Widget agregado: ${widget.label}", Toast.LENGTH_SHORT).show()
                }
            },
            onWidgetRemoved = { widget ->
                selectedWidgets.removeAll { it.packageName == widget.packageName && it.className == widget.className }
                widgetManager.saveSelectedWidgets(selectedWidgets)
                updateAdapter()
                Toast.makeText(this, "Widget removido", Toast.LENGTH_SHORT).show()
            }
        )

        recyclerView.adapter = adapter

        findViewById<Button>(R.id.btn_save).setOnClickListener {
            Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show()
            finish()
        }

        loadAvailableWidgets()
    }

    private fun loadAvailableWidgets() {
        val availableWidgets = widgetManager.getAvailableWidgets()
        adapter.setAvailableWidgets(availableWidgets)
        updateAdapter()
    }

    private fun updateAdapter() {
        adapter.updateList(selectedWidgets)
    }
}