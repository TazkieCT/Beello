package com.example.brailly.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.brailly.databinding.ActivityMainBinding
import com.example.brailly.helper.vibrate
import com.example.brailly.utils.BrailleMappings
import com.example.brailly.utils.TtsHelper
import com.example.brailly.utils.enableSwipeGestures
import com.google.android.material.button.MaterialButton

/**
 * MainActivity serves as the core Braille typing simulation.
 *
 * Features:
 * - Six-button Braille input with tactile feedback.
 * - Swipe gestures for text editing: delete, space, speak all, and clear.
 * - Text-to-Speech (TTS) for auditory feedback of entered text.
 * - Supports number mode toggle and Braille character decoding.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var ttsHelper: TtsHelper
    private val brailleDots = BooleanArray(6) { false }
    private val textBuffer = StringBuilder()
    private val handler = Handler(Looper.getMainLooper())
    private var pendingRunnable: Runnable? = null
    private var isNumberMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ttsHelper = TtsHelper(this)

        // Restore saved text
        savedInstanceState?.getString("text_buffer")?.let {
            textBuffer.append(it)
            binding.resultText.text = textBuffer.toString()
        }

        val buttons = listOf(
            binding.button1, binding.button2, binding.button3,
            binding.button4, binding.button5, binding.button6
        )

        setupBrailleButtons(buttons)
        setupSwipeGestures(buttons)
    }

    /** Sets up click listeners for Braille buttons */
    private fun setupBrailleButtons(buttons: List<MaterialButton>) {
        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                brailleDots[index] = !brailleDots[index]
                button.isSelected = brailleDots[index]
                vibrate()

                pendingRunnable?.let { handler.removeCallbacks(it) }

                pendingRunnable = Runnable {
                    val code = brailleDots.joinToString("") { if (it) "1" else "0" }
                    val result = decodeBraille(code)

                    if (result.isNotEmpty()) {
                        textBuffer.append(result)
                        binding.resultText.text = textBuffer.toString()
                        ttsHelper.speak(result, false)
                    } else {
                        ttsHelper.speak("kombinasi tidak dikenal", false)
                    }

                    resetDots(buttons)
                }

                handler.postDelayed(pendingRunnable!!, 500)
            }
        }
    }

    /** Sets up swipe gestures for editing text */
    private fun setupSwipeGestures(buttons: List<MaterialButton>) {
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

    /** Decodes a Braille code string into a character */
    private fun decodeBraille(code: String): String {
        return when {
            code == "001111" -> {
                isNumberMode = !isNumberMode
                "#"
            }
            isNumberMode -> {
                BrailleMappings.numberMap[code] ?: run {
                    isNumberMode = false
                    BrailleMappings.letterMap[code] ?: BrailleMappings.symbolMap[code] ?: ""
                }
            }
            else -> BrailleMappings.letterMap[code] ?: BrailleMappings.symbolMap[code] ?: ""
        }
    }

    /** Adds a space to the text buffer and speaks "spasi" */
    private fun addSpace() {
        textBuffer.append(" ")
        binding.resultText.text = textBuffer.toString()
        ttsHelper.speak("spasi", false)
    }

    /** Deletes the last character and announces it */
    private fun deleteLast() {
        if (textBuffer.isNotEmpty()) {
            val removed = textBuffer.last()
            textBuffer.deleteCharAt(textBuffer.length - 1)
            binding.resultText.text = textBuffer.toString()
            ttsHelper.speak("hapus $removed", false)
        }
    }

    /** Speaks the entire text buffer */
    private fun speakAll() {
        if (textBuffer.isNotEmpty()) ttsHelper.speak(textBuffer.toString(), false)
        else ttsHelper.speak("tidak ada teks", false)
    }

    /** Clears all text and announces the action */
    private fun clearText() {
        textBuffer.clear()
        binding.resultText.text = ""
        ttsHelper.speak("teks dihapus", false)
    }

    /** Resets Braille dots and button states */
    private fun resetDots(buttons: List<MaterialButton>) {
        for (i in brailleDots.indices) {
            brailleDots[i] = false
            buttons[i].isSelected = false
            buttons[i].isChecked = false
        }
    }

    /** Executes pending Braille decoding immediately */
    private fun flushPending(buttons: List<MaterialButton>) {
        pendingRunnable?.let {
            handler.removeCallbacks(it)
            it.run()
            pendingRunnable = null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("text_buffer", textBuffer.toString())
    }

    override fun onDestroy() {
        ttsHelper.stop()
        ttsHelper.shutdown()
        super.onDestroy()
    }
}
