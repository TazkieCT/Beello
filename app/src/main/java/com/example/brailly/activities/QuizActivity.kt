package com.example.brailly.activities

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.brailly.R
import com.example.brailly.databinding.ActivityQuizBinding
import com.example.brailly.helper.vibrate
import com.example.brailly.models.Animal
import com.example.brailly.utils.BrailleMappings
import com.example.brailly.utils.TtsHelper
import com.example.brailly.utils.enableSwipeGestures
import com.google.android.material.button.MaterialButton
import java.util.Locale

/**
 * QuizActivity allows users to play an interactive animal guessing game.
 *
 * Features:
 * - Braille input via six buttons.
 * - Swipe gestures for deleting characters, skipping question, replaying sound, and checking answers.
 * - Text-to-Speech (TTS) provides instructions, feedback, and hints.
 * - MediaPlayer plays animal sounds associated with each question.
 */
class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var ttsHelper: TtsHelper
    private var mediaPlayer: MediaPlayer? = null

    private val animals = listOf(
        Animal("KUCING", R.raw.cat, "Kucing"),
        Animal("ANJING", R.raw.dog, "Anjing"),
        Animal("AYAM", R.raw.chicken, "Ayam")
    )

    private var currentAnimal: Animal? = null
    private var currentIndex = 0

    private val brailleDots = BooleanArray(6)
    private val textBuffer = StringBuilder()
    private val handler = Handler(Looper.getMainLooper())
    private var pendingRunnable: Runnable? = null
    private var isNumberMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ttsHelper = TtsHelper(this)

        val buttons = listOf(
            binding.button1, binding.button2, binding.button3,
            binding.button4, binding.button5, binding.button6
        )

        setupBrailleButtons(buttons)
        setupSwipeGestures(buttons)
        startQuiz()
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
                handler.postDelayed(pendingRunnable!!, 300)
            }
        }
    }

    /** Sets up swipe gestures for quiz actions */
    private fun setupSwipeGestures(buttons: List<MaterialButton>) {
        binding.root.enableSwipeGestures(
            onSwipeLeft = { deleteLast() },
            onSwipeRight = { skipQuestion() },
            onSwipeUp = { playAnimalSound() },
            onSwipeDown = { checkAnswer() }
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

    /** Starts the quiz with a countdown */
    private fun startQuiz() {
        currentIndex = 0
        textBuffer.clear()
        binding.resultText.text = "Bersiap untuk quiz..."
        ttsHelper.speak("Bersiaplah, quiz akan segera dimulai dalam tiga detik", false)

        Handler(Looper.getMainLooper()).postDelayed({ ttsHelper.speak("Tiga", false) }, 1000)
        Handler(Looper.getMainLooper()).postDelayed({ ttsHelper.speak("Dua", false) }, 2000)
        Handler(Looper.getMainLooper()).postDelayed({ ttsHelper.speak("Satu", false) }, 3000)
        Handler(Looper.getMainLooper()).postDelayed({ nextQuestion() }, 4000)
    }

    /** Advances to the next question */
    private fun nextQuestion() {
        if (currentIndex >= animals.size) {
            ttsHelper.speak("Selamat, semua hewan sudah ditebak!", false)
            return
        }

        currentAnimal = animals[currentIndex++]
        textBuffer.clear()
        binding.resultText.text = "Tebak hewan ini..."
        ttsHelper.speak("Soal nomor $currentIndex. Dengarkan baik baik.", false)

        handler.postDelayed({ playAnimalSound() }, 4000)
    }

    /** Plays the current animal's sound */
    private fun playAnimalSound() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = currentAnimal?.let { MediaPlayer.create(this, it.soundRes) }
        mediaPlayer?.start()
    }

    /** Deletes the last character */
    private fun deleteLast() {
        if (textBuffer.isNotEmpty()) {
            val removed = textBuffer.last()
            textBuffer.deleteCharAt(textBuffer.length - 1)
            binding.resultText.text = textBuffer.toString()
            ttsHelper.speak("hapus $removed", false)
        }
    }

    /** Skips the current question and announces the answer */
    private fun skipQuestion() {
        ttsHelper.speak("Jawabannya adalah ${currentAnimal?.answer}", false)
        nextQuestion()
    }

    /** Checks the user's answer */
    private fun checkAnswer() {
        val userAnswer = textBuffer.toString().uppercase(Locale.getDefault())
        val correctAnswer = currentAnimal?.answer?.uppercase(Locale.getDefault())
        if (userAnswer == correctAnswer) {
            ttsHelper.speak("Benar! Ini adalah ${currentAnimal?.hint}", false)
            handler.postDelayed({ nextQuestion() }, 3000)
        } else {
            ttsHelper.speak("Salah, coba lagi", false)
        }
    }

    /** Resets Braille dots and buttons */
    private fun resetDots(buttons: List<MaterialButton>) {
        for (i in brailleDots.indices) {
            brailleDots[i] = false
            buttons[i].isSelected = false
            buttons[i].isChecked = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        ttsHelper.stop()
        ttsHelper.shutdown()
    }
}
