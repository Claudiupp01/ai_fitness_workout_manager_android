package com.example.ai_fitness_workout_manager.model

data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false // For showing typing indicator
) {
    companion object {
        fun userMessage(content: String): ChatMessage {
            return ChatMessage(
                content = content,
                isFromUser = true
            )
        }

        fun aiMessage(content: String): ChatMessage {
            return ChatMessage(
                content = content,
                isFromUser = false
            )
        }

        fun loadingMessage(): ChatMessage {
            return ChatMessage(
                content = "",
                isFromUser = false,
                isLoading = true
            )
        }
    }
}
