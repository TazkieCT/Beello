package com.example.brailly.activities

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.util.*
import com.example.brailly.R
import com.example.brailly.databinding.ActivityQuizBinding
import com.example.brailly.utils.BrailleMappings
import com.example.brailly.utils.enableSwipeGestures

class QuizActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var binding: ActivityQuizBinding
    private var mediaPlayer: MediaPlayer? = null

    private val animals = listOf(
        Animal("KUCING", R.raw.cat, "Kucing"),
        Animal("ANJING", R.raw.dog, "Anjing"),
        Animal("AYAM", R.raw.chicken, "Ayam"),
    )

    private var currentAnimal: Animal? = null
    private var currentIndex = 0

    private val brailleDots = BooleanArray(6) { false }
    private val textBuffer = StringBuilder()
    private val handler = Handler(Looper.getMainLooper())
    private var pendingRunnable: Runnable? = null
    private var isNumberMode = false

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tts = TextToSpeech(this, this)

        val buttons = listOf(
            binding.button1, binding.button2, binding.button3,
            binding.button4, binding.button5, binding.button6
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
                handler.postDelayed(pendingRunnable!!, 300)
            }
        }

        binding.root.enableSwipeGestures(
            onSwipeLeft = {
                if (textBuffer.isNotEmpty()) {
                    val removed = textBuffer.last()
                    textBuffer.deleteCharAt(textBuffer.length - 1)
                    binding.textView.text = textBuffer.toString()
                    speak("hapus $removed")
                }
            },
            onSwipeRight = {
                speak("Jawabannya adalah ${currentAnimal?.answer}")
                nextQuestion()
            },
            onSwipeUp = {
                playAnimalSound()
            },
            onSwipeDown = {
                checkAnswer()
            }
        )

        startQuiz()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        tts.shutdown()
    }

    private fun startQuiz() {
        currentIndex = 0
        nextQuestion()
    }

    private fun nextQuestion() {
        if (currentIndex >= animals.size) {
            speak("Selamat, semua hewan sudah ditebak!")
            return
        }
        currentAnimal = animals[currentIndex]
        currentIndex++

        textBuffer.clear()
        binding.textView.text = "Tebak hewan ini..."

        playAnimalSound()
    }

    private fun playAnimalSound() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }

        mediaPlayer = currentAnimal?.let { MediaPlayer.create(this, it.soundRes) }

        mediaPlayer?.start()
    }

    private fun speak(text: String, flush: Boolean = true) {
        val queueMode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        tts.speak(text, queueMode, null, null)
    }


    private fun checkAnswer() {
        val userAnswer = textBuffer.toString().uppercase(Locale.getDefault())
        val correctAnswer = currentAnimal?.answer?.uppercase(Locale.getDefault())

        if (userAnswer == correctAnswer) {
            speak("Benar! Ini adalah ${currentAnimal?.hint}")
            nextQuestion()
        } else {
            speak("Salah, coba lagi")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale("id", "ID")
        }
    }

    private fun resetDots(buttons: List<MaterialButton>) {
        for (i in brailleDots.indices) {
            brailleDots[i] = false
            buttons[i].isSelected = false
            buttons[i].isChecked = false
        }
    }
}

data class Animal(
    val answer: String,
    val soundRes: Int,
    val hint: String
)