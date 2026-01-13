package com.example.ai_fitness_workout_manager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_fitness_workout_manager.R

class StepAdapter : RecyclerView.Adapter<StepAdapter.StepViewHolder>() {

    private var steps = listOf<String>()

    fun submitList(newSteps: List<String>) {
        steps = newSteps
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_step, parent, false)
        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.bind(position + 1, steps[position])
    }

    override fun getItemCount() = steps.size

    inner class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStepNumber: TextView = itemView.findViewById(R.id.tvStepNumber)
        private val tvStepDescription: TextView = itemView.findViewById(R.id.tvStepDescription)

        fun bind(stepNumber: Int, description: String) {
            tvStepNumber.text = stepNumber.toString()
            tvStepDescription.text = description
        }
    }
}
