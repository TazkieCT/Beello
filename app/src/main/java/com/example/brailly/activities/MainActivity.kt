package com.example.brailly.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import com.example.brailly.databinding.ActivityMainBinding
import java.util.*
import androidx.core.view.GestureDetectorCompat
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.example.brailly.utils.enableSwipeGestures
import com.google.android.material.button.MaterialButton
import kotlin.math.abs

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var binding: ActivityMainBinding

    private val brailleDots = BooleanArray(6) { false }
    private val textBuffer = StringBuilder()
    private val handler = Handler(Looper.getMainLooper())
    private var pendingRunnable: Runnable? = null

    private val letterMap = mapOf(
        "100000" to "A", "110000" to "B", "100100" to "C", "100110" to "D",
        "100010" to "E", "110100" to "F", "110110" to "G", "110010" to "H",
        "010100" to "I", "010110" to "J", "101000" to "K", "111000" to "L",
        "101100" to "M", "101110" to "N", "101010" to "O", "111100" to "P",
        "111110" to "Q", "111010" to "R", "011100" to "S", "011110" to "T",
        "101001" to "U", "111001" to "V", "010111" to "W", "101101" to "X",
        "101111" to "Y", "101011" to "Z"
    )

    private val numberMap = mapOf(
        "100000" to "1", "110000" to "2", "100100" to "3", "100110" to "4",
        "100010" to "5", "110100" to "6", "110110" to "7", "110010" to "8",
        "010100" to "9", "010110" to "0"
    )

    private val symbolMap = mapOf(
        "001111" to "#", "010000" to ",", "010011" to ".", "011001" to "?",
        "001000" to "'", "001001" to "-", "011010" to "!"
    )

    private var isNumberMode = false

    private fun decodeBraille(code: String): String {
        return when {
            code == "001111" -> {
                isNumberMode = !isNumberMode
                "#"
            }
            isNumberMode -> {
                val num = numberMap[code]
                if (num != null) num
                else {
                    isNumberMode = false
                    letterMap[code] ?: symbolMap[code] ?: ""
                }
            }
            else -> letterMap[code] ?: symbolMap[code] ?: ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tts = TextToSpeech(this, this)

        if (savedInstanceState != null) {
            savedInstanceState.getString("text_buffer")?.let {
                textBuffer.append(it)
                binding.textView.text = textBuffer.toString()
            }
        }

        val buttons = listOf(
            binding.button1,
            binding.button2,
            binding.button3,
            binding.button4,
            binding.button5,
            binding.button6
        )

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                brailleDots[index] = !brailleDots[index]
                button.isSelected = brailleDots[index]

                pendingRunnable?.let { handler.removeCallbacks(it) }

                pendingRunnable = Runnable {
                    val code = brailleDots.joinToString("") { if (it) "1" else "0" }
                    val result = decodeBraille(code)

                    if (result.isNotEmpty()) {
                        textBuffer.append(result)
                        binding.textView.text = textBuffer.toString()
                        speak(result)
                    } else {
                        speak("kombinasi tidak dikenal")
                    }

                    resetDots(buttons)
                }

                handler.postDelayed(pendingRunnable!!, 500)
            }
        }

        binding.root.enableSwipeGestures(
            onSwipeLeft = {
                flushPending(buttons)
                deleteLast()
            },
            onSwipeRight = {
                flushPending(buttons)
                addSpace()
            },
            onSwipeUp = {
                flushPending(buttons)
                speakAll()
            },
            onSwipeDown = {
                flushPending(buttons)
                clearText()
            }
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("text_buffer", textBuffer.toString())
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("id", "ID"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                println("Bahasa tidak didukung")
            } else {
//                speak("Selamat datang di aplikasi Braille")
            }
        } else {
            println("Inisialisasi TTS gagal")
        }
    }

    private fun addSpace() {
        textBuffer.append(" ")
        binding.textView.text = textBuffer.toString()
        speak("spasi")
    }

    private fun deleteLast() {
        if (textBuffer.isNotEmpty()) {
            val removed = textBuffer.last()
            textBuffer.deleteCharAt(textBuffer.length - 1)
            binding.textView.text = textBuffer.toString()
            speak("hapus $removed")
        }
    }

    private fun speakAll() {
        if (textBuffer.isNotEmpty()) {
            speak(textBuffer.toString())
        } else {
            speak("tidak ada teks")
        }
    }

    private fun clearText() {
        textBuffer.clear()
        binding.textView.text = ""
        speak("teks dihapus")
    }

    private fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun resetDots(buttons: List<MaterialButton>) {
        for (i in brailleDots.indices) {
            brailleDots[i] = false
            buttons[i].isSelected = false
            buttons[i].isChecked = false
        }
    }

    private fun flushPending(buttons: List<MaterialButton>) {
        pendingRunnable?.let {
            handler.removeCallbacks(it)
            it.run()
            pendingRunnable = null
        }
    }
}