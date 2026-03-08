package com.romaster.floatingwidget

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainLauncherActivity : Activity() {

    private lateinit var appWidgetManager: AppWidgetManager
    private lateinit var widgetHost: AppWidgetHost
    private lateinit var widgetManager: WidgetManager
    private lateinit var widgetContainer: RecyclerView
    private lateinit var widgetAdapter: LauncherWidgetAdapter

    companion object {
        const val HOST_ID = 1024
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        appWidgetManager = AppWidgetManager.getInstance(this)
        widgetHost = AppWidgetHost(this, HOST_ID)
        widgetHost.startListening()
        widgetManager = WidgetManager(this)

        setupViews()
        loadWidgets()

        findViewById<Button>(R.id.btn_enter_overlay).setOnClickListener {
            startFloatingMode()
        }

        findViewById<Button>(R.id.btn_configure).setOnClickListener {
            startActivity(Intent(this, WidgetConfigurationActivity::class.java))
        }
    }

    private fun setupViews() {
        widgetContainer = findViewById(R.id.widget_container)
        widgetContainer.layoutManager = GridLayoutManager(this, 2)

        widgetAdapter = LauncherWidgetAdapter(
            onWidgetClick = { widgetInfo ->
                Toast.makeText(this, "Widget: ${widgetInfo.label}", Toast.LENGTH_SHORT).show()
            },
            onWidgetLongClick = { widgetInfo ->
                removeWidget(widgetInfo)
            }
        )
        widgetContainer.adapter = widgetAdapter
    }

    private fun loadWidgets() {
        val selectedWidgets = widgetManager.getSelectedWidgets()
        val widgetViews = mutableListOf<WidgetHostView>()

        for (widgetInfo in selectedWidgets) {
            val provider = ComponentName(widgetInfo.packageName, widgetInfo.className)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(provider)

            if (appWidgetIds.isNotEmpty()) {
                try {
                    val hostView = widgetHost.createView(this, appWidgetIds[0],
                        appWidgetManager.getAppWidgetInfo(appWidgetIds[0]))
                    widgetViews.add(hostView as WidgetHostView)
                } catch (e: Exception) {
                    // Error al crear vista
                }
            }
        }

        widgetAdapter.submitList(widgetViews)
    }

    private fun removeWidget(widgetInfo: WidgetInfo) {
        widgetManager.removeWidget(widgetInfo)
        loadWidgets()
        Toast.makeText(this, "Widget removido", Toast.LENGTH_SHORT).show()
    }

    private fun startFloatingMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, 1001)
        } else {
            Intent(this, FloatingLauncherService::class.java).apply {
                startService(this)
                moveTaskToBack(true)
            }
        }
    }

    override fun onDestroy() {
        widgetHost.stopListening()
        super.onDestroy()
    }
}