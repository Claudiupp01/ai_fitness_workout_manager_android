package com.example.ai_fitness_workout_manager.adapter

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_fitness_workout_manager.R
import com.example.ai_fitness_workout_manager.model.ChatMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(ChatDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_USER = 0
        private const val VIEW_TYPE_AI = 1
        private const val VIEW_TYPE_LOADING = 2
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return when {
            message.isLoading -> VIEW_TYPE_LOADING
            message.isFromUser -> VIEW_TYPE_USER
            else -> VIEW_TYPE_AI
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val view = inflater.inflate(R.layout.item_message_user, parent, false)
                UserMessageViewHolder(view)
            }
            VIEW_TYPE_LOADING -> {
                val view = inflater.inflate(R.layout.item_message_loading, parent, false)
                LoadingViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_message_ai, parent, false)
                AIMessageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is UserMessageViewHolder -> holder.bind(message)
            is AIMessageViewHolder -> holder.bind(message)
            is LoadingViewHolder -> holder.bind()
        }
    }

    class UserMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(message: ChatMessage) {
            tvMessage.text = message.content
            tvTime.text = formatTime(message.timestamp)
        }
    }

    class AIMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(message: ChatMessage) {
            tvMessage.text = message.content
            tvTime.text = formatTime(message.timestamp)
        }
    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dot1: View = itemView.findViewById(R.id.dot1)
        private val dot2: View = itemView.findViewById(R.id.dot2)
        private val dot3: View = itemView.findViewById(R.id.dot3)

        private var animator1: ObjectAnimator? = null
        private var animator2: ObjectAnimator? = null
        private var animator3: ObjectAnimator? = null

        fun bind() {
            // Start typing animation
            startTypingAnimation()
        }

        private fun startTypingAnimation() {
            // Animate dots with slight delays
            animator1 = createDotAnimator(dot1, 0L)
            animator2 = createDotAnimator(dot2, 150L)
            animator3 = createDotAnimator(dot3, 300L)

            animator1?.start()
            animator2?.start()
            animator3?.start()
        }

        private fun createDotAnimator(dot: View, startDelay: Long): ObjectAnimator {
            return ObjectAnimator.ofFloat(dot, "alpha", 0.3f, 1f, 0.3f).apply {
                duration = 900
                this.startDelay = startDelay
                repeatCount = ValueAnimator.INFINITE
            }
        }
    }

    class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val format = SimpleDateFormat("h:mm a", Locale.getDefault())
    return format.format(Date(timestamp))
}
