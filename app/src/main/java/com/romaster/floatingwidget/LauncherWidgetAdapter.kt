package com.romaster.floatingwidget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LauncherWidgetAdapter(
    private val onWidgetClick: (WidgetInfo) -> Unit,
    private val onWidgetLongClick: (WidgetInfo) -> Unit
) : RecyclerView.Adapter<LauncherWidgetAdapter.WidgetViewHolder>() {

    private var widgets = listOf<WidgetHostView>()
    private var widgetInfos = listOf<WidgetInfo>()

    fun submitList(hostViews: List<WidgetHostView>, infos: List<WidgetInfo> = listOf()) {
        widgets = hostViews
        widgetInfos = if (infos.isEmpty()) {
            List(hostViews.size) { index -> WidgetInfo("", "", "Widget $index") }
        } else {
            infos
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {
        val container = FrameLayout(parent.context)
        container.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return WidgetViewHolder(container)
    }

    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
        holder.bind(widgets[position], widgetInfos[position])
    }

    override fun getItemCount(): Int = widgets.size

    inner class WidgetViewHolder(private val container: FrameLayout) : 
        RecyclerView.ViewHolder(container) {
        
        fun bind(widgetView: WidgetHostView, widgetInfo: WidgetInfo) {
            container.removeAllViews()
            container.addView(widgetView)
            
            container.setOnClickListener {
                onWidgetClick(widgetInfo)
            }
            
            container.setOnLongClickListener {
                onWidgetLongClick(widgetInfo)
                true
            }
        }
    }
}