package com.example.ai_fitness_workout_manager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OnboardingAdapter(private val items: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imgOnboarding)
        val titleTextView: TextView = view.findViewById(R.id.tvOnboardingTitle)
        val descriptionTextView: TextView = view.findViewById(R.id.tvOnboardingDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_onboarding, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        val item = items[position]
        holder.imageView.setImageResource(item.imageResource)
        holder.titleTextView.text = item.title
        holder.descriptionTextView.text = item.description
    }

    override fun getItemCount(): Int = items.size
}
