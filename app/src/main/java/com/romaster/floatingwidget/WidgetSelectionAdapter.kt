package com.romaster.floatingwidget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WidgetSelectionAdapter(
    private val onWidgetSelected: (WidgetInfo) -> Unit,
    private val onWidgetRemoved: (WidgetInfo) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var availableWidgets = listOf<WidgetInfo>()
    private var selectedWidgets = listOf<WidgetInfo>()

    companion object {
        private const val TYPE_AVAILABLE = 0
        private const val TYPE_SELECTED = 1
    }

    fun setAvailableWidgets(widgets: List<WidgetInfo>) {
        availableWidgets = widgets
        notifyDataSetChanged()
    }

    fun updateList(selected: List<WidgetInfo>) {
        selectedWidgets = selected
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < availableWidgets.size) TYPE_AVAILABLE else TYPE_SELECTED
    }

    override fun getItemCount(): Int = availableWidgets.size + selectedWidgets.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_AVAILABLE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_available_widget, parent, false)
                AvailableViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_selected_widget, parent, false)
                SelectedViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AvailableViewHolder -> {
                if (position < availableWidgets.size) {
                    val widget = availableWidgets[position]
                    holder.bind(widget)
                }
            }
            is SelectedViewHolder -> {
                val selectedIndex = position - availableWidgets.size
                if (selectedIndex < selectedWidgets.size) {
                    val widget = selectedWidgets[selectedIndex]
                    holder.bind(widget)
                }
            }
        }
    }

    inner class AvailableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.widget_name)
        private val btnAdd: Button = itemView.findViewById(R.id.btn_add)

        fun bind(widget: WidgetInfo) {
            tvName.text = widget.label
            btnAdd.setOnClickListener {
                onWidgetSelected(widget)
            }
        }
    }

    inner class SelectedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.widget_name)
        private val btnRemove: Button = itemView.findViewById(R.id.btn_remove)

        fun bind(widget: WidgetInfo) {
            tvName.text = widget.label
            btnRemove.setOnClickListener {
                onWidgetRemoved(widget)
            }
        }
    }
}