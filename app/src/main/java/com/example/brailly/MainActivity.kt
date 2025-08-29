package com.example.brailly

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import com.example.brailly.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var binding: ActivityMainBinding

    private val brailleDots = BooleanArray(6) { false }

    private val textBuffer = StringBuilder()
    private val handler = Handler(Looper.getMainLooper())
    private var pendingRunnable: Runnable? = null

    private val brailleMap = mapOf(
        "100000" to "A",
        "101000" to "B",
        "110000" to "C",
        "110100" to "D",
        "100100" to "E",
        "111000" to "F",
        "111100" to "G",
        "101100" to "H",
        "011000" to "I",
        "011100" to "J",
        "100010" to "K",
        "101010" to "L",
        "110010" to "M",
        "110110" to "N",
        "100110" to "O",
        "111010" to "P",
        "111110" to "Q",
        "101110" to "R",
        "011010" to "S",
        "011110" to "T",
        "100011" to "U",
        "101011" to "V",
        "011101" to "W",
        "110011" to "X",
        "110111" to "Y",
        "100111" to "Z"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tts = TextToSpeech(this, this)

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
                    val result = brailleMap[code]

                    if (result != null) {
                        textBuffer.append(result)
                        binding.textView.text = textBuffer.toString()
                        speak(result)
                    } else {
                        speak("kombinasi tidak dikenal")
                    }

                    resetDots(buttons)
                }
                handler.postDelayed(pendingRunnable!!, 300)
            }
        }

        binding.buttonSpace.setOnClickListener {
            flushPending(buttons)
            textBuffer.append(" ")
            binding.textView.text = textBuffer.toString()
            speak("spasi")
        }

        binding.buttonDelete.setOnClickListener {
            flushPending(buttons)
            if (textBuffer.isNotEmpty()) {
                val removed = textBuffer.last()
                textBuffer.deleteCharAt(textBuffer.length - 1)
                binding.textView.text = textBuffer.toString()
                speak("hapus $removed")
            }
        }

        binding.buttonSpeakAll.setOnClickListener {
            flushPending(buttons)
            if (textBuffer.isNotEmpty()) {
                speak(textBuffer.toString())
            } else {
                speak("tidak ada teks")
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("id", "ID"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                println("Bahasa tidak didukung")
            } else {
                speak("Selamat datang di aplikasi Braille")
            }
        } else {
            println("Inisialisasi TTS gagal")
        }
    }

    private fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun resetDots(buttons: List<android.view.View>) {
        for (i in brailleDots.indices) {
            brailleDots[i] = false
            buttons[i].isSelected = false
        }
    }

    private fun flushPending(buttons: List<android.view.View>) {
        pendingRunnable?.let {
            handler.removeCallbacks(it)
            it.run()
            pendingRunnable = null
        }
    }
}
