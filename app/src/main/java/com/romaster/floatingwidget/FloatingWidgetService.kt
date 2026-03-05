package com.romaster.floatingwidget

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ViewFlipper
import androidx.viewpager2.widget.ViewPager2

class FloatingWidgetService : Service() {
    
    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var viewFlipper: ViewFlipper
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        setupFloatingView()
    }
    
    private fun setupFloatingView() {
        // Inflar el layout del overlay
        floatingView = LayoutInflater.from(this).inflate(R.layout.overlay_widget_container, null)
        
        // Configurar parámetros de la ventana
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
            x = 100  // Posición inicial
            y = 100
        }
        
        // Hacer el overlay arrastrable
        floatingView.setOnTouchListener { view, event ->
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
        
        // Configurar ViewFlipper para los widgets
        viewFlipper = floatingView.findViewById(R.id.widget_flipper)
        
        // Botones de navegación
        val btnPrev = floatingView.findViewById<Button>(R.id.btn_prev)
        val btnNext = floatingView.findViewById<Button>(R.id.btn_next)
        
        btnPrev.setOnClickListener {
            viewFlipper.showPrevious()
        }
        
        btnNext.setOnClickListener {
            viewFlipper.showNext()
        }
        
        // Agregar la vista al WindowManager
        windowManager.addView(floatingView, params)
        
        // Cargar widgets guardados
        loadWidgets()
    }
    
    private fun loadWidgets() {
        val widgetManager = WidgetManager(this)
        val widgets = widgetManager.getWidgetList()
        
        // Aquí cargaremos los widgets en el ViewFlipper
        // Por ahora solo añadimos vistas de ejemplo
        for (i in widgets.indices) {
            val dummyView = LayoutInflater.from(this).inflate(R.layout.widget_placeholder, null)
            viewFlipper.addView(dummyView)
        }
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        if (::floatingView.isInitialized) {
            windowManager.removeView(floatingView)
        }
    }
}