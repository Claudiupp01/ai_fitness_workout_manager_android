package com.example.ai_fitness_workout_manager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_fitness_workout_manager.R
import com.example.ai_fitness_workout_manager.model.DayItem

class DayAdapter(
    private var days: List<DayItem>,
    private val onDaySelected: (DayItem) -> Unit
) : RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    private var selectedPosition = days.indexOfFirst { it.isSelected }.takeIf { it >= 0 } ?: 0

    fun updateDays(newDays: List<DayItem>) {
        days = newDays
        selectedPosition = days.indexOfFirst { it.isSelected }.takeIf { it >= 0 } ?: 0
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(days[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = days.size

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val container: LinearLayout = itemView.findViewById(R.id.dayContainer)
        private val tvDayOfWeek: TextView = itemView.findViewById(R.id.tvDayOfWeek)
        private val tvDayOfMonth: TextView = itemView.findViewById(R.id.tvDayOfMonth)

        fun bind(day: DayItem, isSelected: Boolean) {
            tvDayOfWeek.text = day.dayOfWeek
            tvDayOfMonth.text = day.dayOfMonth.toString()

            val context = itemView.context

            when {
                isSelected -> {
                    container.setBackgroundResource(R.drawable.bg_day_selected)
                    tvDayOfWeek.setTextColor(ContextCompat.getColor(context, R.color.white))
                    tvDayOfMonth.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
                day.isToday -> {
                    container.setBackgroundResource(R.drawable.bg_day_today)
                    tvDayOfWeek.setTextColor(ContextCompat.getColor(context, R.color.primaryColor))
                    tvDayOfMonth.setTextColor(ContextCompat.getColor(context, R.color.primaryColor))
                }
                else -> {
                    container.setBackgroundResource(R.drawable.bg_day_default)
                    tvDayOfWeek.setTextColor(ContextCompat.getColor(context, R.color.textSecondary))
                    tvDayOfMonth.setTextColor(ContextCompat.getColor(context, R.color.textPrimary))
                }
            }

            itemView.setOnClickListener {
                val oldPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(oldPosition)
                notifyItemChanged(selectedPosition)
                onDaySelected(day)
            }
        }
    }
}
