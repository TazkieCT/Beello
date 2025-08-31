package com.example.brailly.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

/**
 * Safe TTS helper class that waits until TTS engine is ready before speaking.
 *
 * @param context Context for initializing TTS
 * @param language Locale to use for TTS (default Indonesian)
 */
class TtsHelper(context: Context, private val language: Locale = Locale("id", "ID")) {

    private var tts: TextToSpeech? = null
    private var isReady = false
    private val pendingQueue = mutableListOf<String>()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = language
                isReady = true
                // Speak any queued text
                pendingQueue.forEach { tts?.speak(it, TextToSpeech.QUEUE_ADD, null, null) }
                pendingQueue.clear()
            }
        }
    }

    /**
     * Speaks text safely. If TTS not ready yet, queues it.
     *
     * @param text The text to speak
     * @param flush True to flush queue, false to add
     */
    fun speak(text: String, flush: Boolean) {
        if (isReady) {
            val mode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            tts?.speak(text, mode, null, null)
        } else {
            pendingQueue.add(text)
        }
    }

    /** Stops speaking immediately */
    fun stop() {
        tts?.stop()
    }

    /** Shuts down TTS engine */
    fun shutdown() {
        tts?.shutdown()
        tts = null
        isReady = false
    }
}
