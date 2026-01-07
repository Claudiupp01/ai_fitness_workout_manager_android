package com.example.ai_fitness_workout_manager.ai

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

/**
 * Manages text-to-speech functionality using Android's built-in TextToSpeech
 *
 * How it works:
 * 1. Initializes TextToSpeech engine
 * 2. Synthesizes text into speech audio
 * 3. Plays through device speakers
 * 4. Provides playback controls (play, pause, stop)
 */
class TextToSpeechManager(private val context: Context) {

    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false
    private var isSpeaking = false
    private var currentUtteranceId: String? = null

    // Callbacks
    private var onInitializedCallback: (() -> Unit)? = null
    private var onStartCallback: (() -> Unit)? = null
    private var onDoneCallback: (() -> Unit)? = null
    private var onErrorCallback: ((String) -> Unit)? = null

    /**
     * Initialize the TextToSpeech engine
     * Must be called before speak()
     */
    fun initialize(onInitialized: (() -> Unit)? = null) {
        onInitializedCallback = onInitialized

        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Set language to device default
                val result = textToSpeech?.setLanguage(Locale.getDefault())

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Fallback to English if device language not supported
                    textToSpeech?.setLanguage(Locale.US)
                }

                // Configure speech parameters
                textToSpeech?.setPitch(1.0f) // Normal pitch
                textToSpeech?.setSpeechRate(1.0f) // Normal speed

                // Set utterance listener for playback events
                textToSpeech?.setOnUtteranceProgressListener(utteranceListener)

                isInitialized = true
                onInitializedCallback?.invoke()
            } else {
                isInitialized = false
                onErrorCallback?.invoke("Failed to initialize text-to-speech")
            }
        }
    }

    /**
     * Speak the given text
     * @param text The text to speak
     * @param utteranceId Unique ID for this speech request (optional)
     */
    fun speak(text: String, utteranceId: String = "tts_${System.currentTimeMillis()}") {
        if (!isInitialized) {
            onErrorCallback?.invoke("Text-to-speech not initialized")
            return
        }

        if (text.isEmpty()) {
            return
        }

        currentUtteranceId = utteranceId

        // Stop any current speech
        stop()

        // Speak the text
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        isSpeaking = true
    }

    /**
     * Stop speaking
     */
    fun stop() {
        if (isSpeaking) {
            textToSpeech?.stop()
            isSpeaking = false
            currentUtteranceId = null
        }
    }

    /**
     * Check if currently speaking
     */
    fun isSpeaking(): Boolean {
        return textToSpeech?.isSpeaking == true
    }

    /**
     * Set speech rate (0.5 = slow, 1.0 = normal, 2.0 = fast)
     */
    fun setSpeechRate(rate: Float) {
        textToSpeech?.setSpeechRate(rate.coerceIn(0.1f, 3.0f))
    }

    /**
     * Set pitch (0.5 = low, 1.0 = normal, 2.0 = high)
     */
    fun setPitch(pitch: Float) {
        textToSpeech?.setPitch(pitch.coerceIn(0.1f, 2.0f))
    }

    /**
     * Set language
     */
    fun setLanguage(locale: Locale): Boolean {
        val result = textToSpeech?.setLanguage(locale)
        return result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
    }

    /**
     * Clean up resources
     * Call this in onDestroy()
     */
    fun shutdown() {
        stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
    }

    /**
     * Set callback for when TTS starts speaking
     */
    fun setOnStartListener(callback: () -> Unit) {
        onStartCallback = callback
    }

    /**
     * Set callback for when TTS finishes speaking
     */
    fun setOnDoneListener(callback: () -> Unit) {
        onDoneCallback = callback
    }

    /**
     * Set callback for errors
     */
    fun setOnErrorListener(callback: (String) -> Unit) {
        onErrorCallback = callback
    }

    /**
     * UtteranceProgressListener implementation
     * Handles TTS playback events
     */
    private val utteranceListener = object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
            // Called when TTS starts speaking
            if (utteranceId == currentUtteranceId) {
                isSpeaking = true
                onStartCallback?.invoke()
            }
        }

        override fun onDone(utteranceId: String?) {
            // Called when TTS finishes speaking
            if (utteranceId == currentUtteranceId) {
                isSpeaking = false
                currentUtteranceId = null
                onDoneCallback?.invoke()
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onError(utteranceId: String?) {
            // Called on error (deprecated, but still needed for older devices)
            if (utteranceId == currentUtteranceId) {
                isSpeaking = false
                currentUtteranceId = null
                onErrorCallback?.invoke("Speech synthesis error")
            }
        }

        override fun onError(utteranceId: String?, errorCode: Int) {
            // Called on error (new method)
            if (utteranceId == currentUtteranceId) {
                isSpeaking = false
                currentUtteranceId = null
                val errorMessage = when (errorCode) {
                    TextToSpeech.ERROR_SYNTHESIS -> "Synthesis error"
                    TextToSpeech.ERROR_SERVICE -> "Service error"
                    TextToSpeech.ERROR_OUTPUT -> "Output error"
                    TextToSpeech.ERROR_NETWORK -> "Network error"
                    TextToSpeech.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    TextToSpeech.ERROR_INVALID_REQUEST -> "Invalid request"
                    TextToSpeech.ERROR_NOT_INSTALLED_YET -> "TTS not installed"
                    else -> "Unknown error"
                }
                onErrorCallback?.invoke(errorMessage)
            }
        }

        override fun onStop(utteranceId: String?, interrupted: Boolean) {
            // Called when TTS is stopped
            if (utteranceId == currentUtteranceId) {
                isSpeaking = false
                currentUtteranceId = null
            }
        }
    }
}
