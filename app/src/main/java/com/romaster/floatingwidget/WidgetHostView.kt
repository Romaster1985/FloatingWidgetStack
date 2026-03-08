package com.romaster.floatingwidget

import android.appwidget.AppWidgetHostView
import android.content.Context
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

class WidgetHostView(context: Context) : AppWidgetHostView(context) {

    override fun getDefaultView(): FrameLayout {
        return FrameLayout(context).apply {
            addView(TextView(context).apply {
                text = "Cargando widget..."
                setTextColor(ContextCompat.getColor(context, android.R.color.white))
                setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            })
        }
    }

    override fun getErrorView(): FrameLayout {
        return FrameLayout(context).apply {
            addView(TextView(context).apply {
                text = "Error al cargar widget"
                setTextColor(ContextCompat.getColor(context, android.R.color.white))
                setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            })
        }
    }
}