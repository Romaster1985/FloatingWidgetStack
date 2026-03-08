package com.romaster.floatingwidget

import android.app.Service
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.ViewFlipper

class FloatingLauncherService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var viewFlipper: ViewFlipper
    private lateinit var txtPageIndicator: TextView
    private lateinit var widgetHost: AppWidgetHost
    private lateinit var widgetManager: WidgetManager
    private lateinit var appWidgetManager: AppWidgetManager

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        widgetManager = WidgetManager(this)
        appWidgetManager = AppWidgetManager.getInstance(this)
        widgetHost = AppWidgetHost(this, MainLauncherActivity.HOST_ID)
        widgetHost.startListening()
        setupFloatingView()
    }

    private fun setupFloatingView() {
        floatingView = LayoutInflater.from(this).inflate(R.layout.overlay_container, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }

        setupDragHandle(floatingView.findViewById(R.id.drag_handle), params)
        setupNavigation(floatingView)
        setupCloseButton(floatingView)

        windowManager.addView(floatingView, params)
        loadWidgets()
    }

    private fun setupDragHandle(dragHandle: View, params: WindowManager.LayoutParams) {
        dragHandle.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(floatingView, params)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupNavigation(view: View) {
        viewFlipper = view.findViewById(R.id.widget_flipper)
        txtPageIndicator = view.findViewById(R.id.txt_page_indicator)

        view.findViewById<Button>(R.id.btn_prev).setOnClickListener {
            if (viewFlipper.childCount > 0) {
                viewFlipper.showPrevious()
                updatePageIndicator()
            }
        }

        view.findViewById<Button>(R.id.btn_next).setOnClickListener {
            if (viewFlipper.childCount > 0) {
                viewFlipper.showNext()
                updatePageIndicator()
            }
        }
    }

    private fun setupCloseButton(view: View) {
        view.findViewById<ImageView>(R.id.btn_close).setOnClickListener {
            stopSelf()
        }
    }

    private fun loadWidgets() {
        viewFlipper.removeAllViews()
        
        val selectedWidgets = widgetManager.getSelectedWidgets()
        
        if (selectedWidgets.isEmpty()) {
            showEmptyState()
            return
        }

        for (widgetInfo in selectedWidgets) {
            try {
                val provider = ComponentName(widgetInfo.packageName, widgetInfo.className)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(provider)
                
                if (appWidgetIds.isNotEmpty()) {
                    val hostView = widgetHost.createView(this, appWidgetIds[0],
                        appWidgetManager.getAppWidgetInfo(appWidgetIds[0]))
                    
                    val layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    hostView.layoutParams = layoutParams
                    viewFlipper.addView(hostView)
                }
            } catch (e: Exception) {
                addErrorView(widgetInfo.label)
            }
        }

        updatePageIndicator()
    }

    private fun showEmptyState() {
        val emptyView = LayoutInflater.from(this).inflate(R.layout.widget_placeholder, viewFlipper, false)
        val textView = emptyView.findViewById<TextView>(android.R.id.text1)
        textView?.text = "Sin widgets\nConfigura en el launcher"
        viewFlipper.addView(emptyView)
        txtPageIndicator.text = "0/0"
    }

    private fun addErrorView(widgetLabel: String) {
        val errorView = LayoutInflater.from(this).inflate(R.layout.widget_placeholder, viewFlipper, false)
        val textView = errorView.findViewById<TextView>(android.R.id.text1)
        textView?.text = "Error: $widgetLabel"
        textView?.setBackgroundColor(0xFFFF4444.toInt())
        viewFlipper.addView(errorView)
    }

    private fun updatePageIndicator() {
        val current = viewFlipper.displayedChild + 1
        val total = viewFlipper.childCount
        txtPageIndicator.text = "$current/$total"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        widgetHost.stopListening()
        if (::floatingView.isInitialized) {
            try {
                windowManager.removeView(floatingView)
            } catch (e: Exception) {
                // Ignorar
            }
        }
        super.onDestroy()
    }
}