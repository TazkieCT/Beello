package com.example.brailly.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

class TtsHelper(context: Context) {

    private var tts: TextToSpeech? = null
    private var isReady = false
    private val pendingQueue = mutableListOf<String>()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val phoneLocale = Locale.getDefault()
                val language = if (phoneLocale.language == "id") {
                    Locale("id", "ID")
                } else {
                    Locale.ENGLISH
                }

                val result = tts?.setLanguage(language)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // fallback to English if Indonesian is not available
                    tts?.setLanguage(Locale.ENGLISH)
                }

                isReady = true
                pendingQueue.forEach { tts?.speak(it, TextToSpeech.QUEUE_ADD, null, null) }
                pendingQueue.clear()
            }
        }
    }

    fun speak(text: String, flush: Boolean) {
        if (isReady) {
            val mode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            tts?.speak(text, mode, null, null)
        } else {
            pendingQueue.add(text)
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.shutdown()
        tts = null
        isReady = false
    }
}
