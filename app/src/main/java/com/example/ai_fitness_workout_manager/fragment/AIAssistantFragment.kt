package com.example.ai_fitness_workout_manager.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_fitness_workout_manager.R
import com.example.ai_fitness_workout_manager.adapter.ChatAdapter
import com.example.ai_fitness_workout_manager.ai.GeminiAIManager
import com.example.ai_fitness_workout_manager.ai.TextToSpeechManager
import com.example.ai_fitness_workout_manager.ai.VoiceInputManager
import com.example.ai_fitness_workout_manager.firebase.FirebaseAuthManager
import com.example.ai_fitness_workout_manager.firebase.FirebaseDbManager
import com.example.ai_fitness_workout_manager.model.ChatMessage
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AIAssistantFragment : Fragment() {

    private lateinit var rvMessages: RecyclerView
    private lateinit var welcomeContainer: LinearLayout
    private lateinit var etMessage: TextInputEditText
    private lateinit var fabSend: FloatingActionButton
    private lateinit var fabMicrophone: FloatingActionButton
    private lateinit var tvStatus: TextView
    private lateinit var btnClearChat: View

    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    private lateinit var voiceInputManager: VoiceInputManager
    private lateinit var ttsManager: TextToSpeechManager

    private var isWaitingForResponse = false
    private var isListening = false

    // Permission launcher for microphone
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startVoiceInput()
        } else {
            Toast.makeText(context, R.string.permission_audio_required, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        fun newInstance(): AIAssistantFragment {
            return AIAssistantFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ai_assistant, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        initializeVoiceManagers()
        setupRecyclerView()
        setupClickListeners()
        initializeAI()
        loadUserProfile()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up voice managers
        voiceInputManager.destroy()
        ttsManager.shutdown()
    }

    private fun initViews(view: View) {
        rvMessages = view.findViewById(R.id.rvMessages)
        welcomeContainer = view.findViewById(R.id.welcomeContainer)
        etMessage = view.findViewById(R.id.etMessage)
        fabSend = view.findViewById(R.id.fabSend)
        fabMicrophone = view.findViewById(R.id.fabMicrophone)
        tvStatus = view.findViewById(R.id.tvStatus)
        btnClearChat = view.findViewById(R.id.btnClearChat)
    }

    private fun initializeVoiceManagers() {
        // Initialize Voice Input Manager
        voiceInputManager = VoiceInputManager(requireContext())

        voiceInputManager.setOnResultListener { transcribedText ->
            // Put transcribed text in input field and send
            etMessage.setText(transcribedText)
            sendMessage()
            updateMicrophoneButton(false)
        }

        voiceInputManager.setOnErrorListener { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            updateMicrophoneButton(false)
        }

        voiceInputManager.setOnStartListeningListener {
            updateMicrophoneButton(true)
        }

        voiceInputManager.setOnEndListeningListener {
            updateMicrophoneButton(false)
        }

        // Initialize Text-to-Speech Manager
        ttsManager = TextToSpeechManager(requireContext())
        ttsManager.initialize()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter { message ->
            // Handle speak button click
            if (ttsManager.isSpeaking()) {
                ttsManager.stop()
            } else {
                ttsManager.speak(message.content, message.id)
            }
        }
        rvMessages.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
        updateWelcomeVisibility()
    }

    private fun setupClickListeners() {
        fabSend.setOnClickListener {
            sendMessage()
        }

        fabMicrophone.setOnClickListener {
            if (isListening) {
                voiceInputManager.stopListening()
            } else {
                checkMicrophonePermissionAndStart()
            }
        }

        etMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }

        btnClearChat.setOnClickListener {
            showClearChatConfirmation()
        }

        // Suggestion chips
        view?.let { v ->
            v.findViewById<Chip>(R.id.chipSuggestion1)?.setOnClickListener {
                setMessageAndSend("Can you create a personalized workout plan for me?")
            }
            v.findViewById<Chip>(R.id.chipSuggestion2)?.setOnClickListener {
                setMessageAndSend("Can you suggest some healthy meal ideas based on my goals?")
            }
            v.findViewById<Chip>(R.id.chipSuggestion3)?.setOnClickListener {
                setMessageAndSend("What are the best strategies to lose weight safely and effectively?")
            }
            v.findViewById<Chip>(R.id.chipSuggestion4)?.setOnClickListener {
                setMessageAndSend("What are the best exercises for building strong abs?")
            }
        }
    }

    private fun initializeAI() {
        GeminiAIManager.initialize()
        if (!GeminiAIManager.hasValidApiKey()) {
            tvStatus.text = "API key not configured"
            tvStatus.setTextColor(resources.getColor(R.color.warning, null))
        }
    }

    private fun loadUserProfile() {
        val userId = FirebaseAuthManager.currentUserId ?: return

        FirebaseDbManager.getUserProfile(userId,
            onSuccess = { profile ->
                GeminiAIManager.setUserProfile(profile)
            },
            onError = { }
        )
    }

    private fun setMessageAndSend(text: String) {
        etMessage.setText(text)
        sendMessage()
    }

    private fun sendMessage() {
        val messageText = etMessage.text?.toString()?.trim() ?: return

        if (messageText.isEmpty()) {
            return
        }

        if (isWaitingForResponse) {
            Toast.makeText(context, "Please wait for the current response", Toast.LENGTH_SHORT).show()
            return
        }

        if (!GeminiAIManager.hasValidApiKey()) {
            Toast.makeText(context, "Please configure your Gemini API key in local.properties", Toast.LENGTH_LONG).show()
            return
        }

        // Clear input
        etMessage.text?.clear()

        // Add user message
        val userMessage = ChatMessage.userMessage(messageText)
        messages.add(userMessage)
        updateMessages()

        // Show typing indicator
        isWaitingForResponse = true
        tvStatus.text = "Typing..."
        val loadingMessage = ChatMessage.loadingMessage()
        messages.add(loadingMessage)
        updateMessages()

        // Send to AI
        lifecycleScope.launch {
            val result = GeminiAIManager.sendMessage(messageText)

            // Remove loading message
            messages.removeIf { it.isLoading }

            result.fold(
                onSuccess = { response ->
                    val aiMessage = ChatMessage.aiMessage(response)
                    messages.add(aiMessage)
                    tvStatus.text = "Online - Ready to help"
                },
                onFailure = { error ->
                    val errorMessage = ChatMessage.aiMessage(
                        "I apologize, but I encountered an error: ${error.message ?: "Unknown error"}. Please try again."
                    )
                    messages.add(errorMessage)
                    tvStatus.text = "Error occurred"
                }
            )

            isWaitingForResponse = false
            updateMessages()
        }
    }

    private fun updateMessages() {
        chatAdapter.submitList(messages.toList()) {
            // Scroll to bottom after list update
            if (messages.isNotEmpty()) {
                rvMessages.scrollToPosition(messages.size - 1)
            }
        }
        updateWelcomeVisibility()
    }

    private fun updateWelcomeVisibility() {
        if (messages.isEmpty()) {
            welcomeContainer.visibility = View.VISIBLE
            rvMessages.visibility = View.GONE
        } else {
            welcomeContainer.visibility = View.GONE
            rvMessages.visibility = View.VISIBLE
        }
    }

    private fun showClearChatConfirmation() {
        if (messages.isEmpty()) {
            Toast.makeText(context, "Chat is already empty", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Clear Chat")
            .setMessage("Are you sure you want to clear the chat history?")
            .setPositiveButton("Clear") { _, _ ->
                clearChat()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun clearChat() {
        messages.clear()
        GeminiAIManager.clearChatHistory()
        updateMessages()
        tvStatus.text = "Online - Ready to help"
        Toast.makeText(context, "Chat cleared", Toast.LENGTH_SHORT).show()
    }

    // ==================== VOICE INPUT METHODS ====================

    private fun checkMicrophonePermissionAndStart() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
                startVoiceInput()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                // Show rationale and request permission
                AlertDialog.Builder(requireContext())
                    .setTitle("Microphone Permission")
                    .setMessage("Microphone permission is needed to use voice input for talking to FitCoach AI.")
                    .setPositiveButton("Grant") { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            else -> {
                // Request permission directly
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startVoiceInput() {
        if (!voiceInputManager.isAvailable()) {
            Toast.makeText(context, R.string.speech_not_available, Toast.LENGTH_SHORT).show()
            return
        }

        tvStatus.text = getString(R.string.listening)
        voiceInputManager.startListening()
    }

    private fun updateMicrophoneButton(listening: Boolean) {
        isListening = listening
        if (listening) {
            // Change icon to indicate listening
            fabMicrophone.setImageResource(R.drawable.ic_microphone)
            fabMicrophone.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.error)
            tvStatus.text = getString(R.string.listening)
        } else {
            // Reset to normal state
            fabMicrophone.setImageResource(R.drawable.ic_microphone)
            fabMicrophone.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.primaryColor)
            tvStatus.text = "Online - Ready to help"
        }
    }
}
