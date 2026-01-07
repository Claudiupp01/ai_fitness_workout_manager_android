package com.example.ai_fitness_workout_manager.ai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

/**
 * Manages speech-to-text functionality using Android's built-in SpeechRecognizer
 *
 * How it works:
 * 1. Creates a SpeechRecognizer instance
 * 2. Listens for audio input from microphone
 * 3. Uses Google's on-device speech recognition
 * 4. Returns transcribed text via callbacks
 */
class VoiceInputManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    companion object {
        private const val TAG = "VoiceInputManager"
    }

    // Callbacks for voice input events
    private var onResultCallback: ((String) -> Unit)? = null
    private var onErrorCallback: ((String) -> Unit)? = null
    private var onReadyCallback: (() -> Unit)? = null
    private var onStartListeningCallback: (() -> Unit)? = null
    private var onEndListeningCallback: (() -> Unit)? = null

    /**
     * Check if speech recognition is available on this device
     */
    fun isAvailable(): Boolean {
        val available = SpeechRecognizer.isRecognitionAvailable(context)
        Log.d(TAG, "Speech recognition available: $available")
        return available
    }

    /**
     * Initialize the speech recognizer
     * Must be called before startListening()
     */
    fun initialize() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(recognitionListener)
        }
    }

    /**
     * Start listening for voice input
     */
    fun startListening() {
        Log.d(TAG, "startListening() called")

        if (!isAvailable()) {
            Log.e(TAG, "Speech recognition not available on this device")
            onErrorCallback?.invoke("Speech recognition not available on this device")
            return
        }

        if (isListening) {
            Log.d(TAG, "Already listening, ignoring start request")
            return // Already listening
        }

        initialize()

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true) // Get partial results while speaking
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        }

        isListening = true
        Log.d(TAG, "Starting speech recognizer with language: ${Locale.getDefault()}")
        speechRecognizer?.startListening(intent)
    }

    /**
     * Stop listening for voice input
     */
    fun stopListening() {
        Log.d(TAG, "stopListening() called")
        if (isListening) {
            speechRecognizer?.stopListening()
            isListening = false
            Log.d(TAG, "Speech recognizer stopped")
        } else {
            Log.d(TAG, "Not listening, nothing to stop")
        }
    }

    /**
     * Cancel listening without processing results
     */
    fun cancel() {
        if (isListening) {
            speechRecognizer?.cancel()
            isListening = false
        }
    }

    /**
     * Clean up resources
     * Call this when done with voice input (e.g., in onDestroy)
     */
    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        isListening = false
    }

    /**
     * Set callback for when speech is recognized
     */
    fun setOnResultListener(callback: (String) -> Unit) {
        onResultCallback = callback
    }

    /**
     * Set callback for errors
     */
    fun setOnErrorListener(callback: (String) -> Unit) {
        onErrorCallback = callback
    }

    /**
     * Set callback for when recognizer is ready
     */
    fun setOnReadyListener(callback: () -> Unit) {
        onReadyCallback = callback
    }

    /**
     * Set callback for when listening starts
     */
    fun setOnStartListeningListener(callback: () -> Unit) {
        onStartListeningCallback = callback
    }

    /**
     * Set callback for when listening ends
     */
    fun setOnEndListeningListener(callback: () -> Unit) {
        onEndListeningCallback = callback
    }

    /**
     * RecognitionListener implementation
     * Handles all speech recognition events
     */
    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            // Called when the recognizer is ready to start listening
            Log.d(TAG, "onReadyForSpeech - Recognizer is ready to listen")
            onReadyCallback?.invoke()
        }

        override fun onBeginningOfSpeech() {
            // Called when user starts speaking
            Log.d(TAG, "onBeginningOfSpeech - User started speaking")
            onStartListeningCallback?.invoke()
        }

        override fun onRmsChanged(rmsdB: Float) {
            // Called for audio level changes (can be used for volume visualization)
            // rmsdB is the sound level in decibels
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            // Called when more sound has been received (not commonly used)
        }

        override fun onEndOfSpeech() {
            // Called when user stops speaking
            Log.d(TAG, "onEndOfSpeech - User stopped speaking")
            isListening = false
            onEndListeningCallback?.invoke()
        }

        override fun onError(error: Int) {
            // Called when an error occurs
            isListening = false
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> "No speech match"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                SpeechRecognizer.ERROR_SERVER -> "Server error"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                else -> "Unknown error"
            }
            Log.e(TAG, "onError - Error code: $error, Message: $errorMessage")
            onErrorCallback?.invoke(errorMessage)
            onEndListeningCallback?.invoke()
        }

        override fun onResults(results: Bundle?) {
            // Called when recognition results are ready
            Log.d(TAG, "onResults - Recognition complete")
            isListening = false

            // Get the list of recognition results
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            Log.d(TAG, "onResults - Matches found: ${matches?.size ?: 0}")

            if (!matches.isNullOrEmpty()) {
                // Return the best match (first result has highest confidence)
                Log.d(TAG, "onResults - Best match: ${matches[0]}")
                onResultCallback?.invoke(matches[0])
            } else {
                Log.w(TAG, "onResults - No speech recognized")
                onErrorCallback?.invoke("No speech recognized")
            }

            onEndListeningCallback?.invoke()
        }

        override fun onPartialResults(partialResults: Bundle?) {
            // Called when partial recognition results are available
            // Can be used to show real-time transcription while user is speaking
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                Log.d(TAG, "onPartialResults - Partial: ${matches[0]}")
                // Optionally show partial results in UI
                // onPartialResultCallback?.invoke(matches[0])
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            // Reserved for future use
        }
    }
}
