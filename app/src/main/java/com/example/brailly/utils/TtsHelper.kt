package com.example.brailly.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

/**
 * TtsHelper is a utility class for simplifying the usage of Android's Text-to-Speech (TTS) engine.
 *
 * Features:
 * - Initializes a TTS instance with a specified language (default is Indonesian).
 * - Provides simple methods to speak text, stop speaking, and shutdown the TTS engine.
 *
 * @param context the context used to initialize the TTS engine
 * @param language the Locale for TTS (default is Indonesian Locale "id-ID")
 */
class TtsHelper(context: Context, private val language: Locale = Locale("id", "ID")) {

    // Internal Text-to-Speech instance
    private var tts: TextToSpeech? = TextToSpeech(context) { status ->
        if (status == TextToSpeech.SUCCESS) tts?.language = language
    }

    /**
     * Speaks the given text immediately, replacing any current speech in the queue.
     *
     * @param text the string to be spoken
     */
    fun speak(text: String, bool: Boolean) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    /** Stops any ongoing speech immediately */
    fun stop() {
        tts?.stop()
    }

    /** Shuts down the TTS engine to release resources */
    fun shutdown() {
        tts?.shutdown()
    }
}
