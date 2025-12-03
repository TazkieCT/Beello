package com.example.brailly.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.brailly.databinding.ActivityCalibrationBinding
import com.example.brailly.helper.vibrate
import com.example.brailly.utils.TtsHelper
import java.util.*

class CalibrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalibrationBinding
    private var ttsHelper: TtsHelper? = null

    private val buttonList by lazy {
        listOf(
            binding.button1,
            binding.button2,
            binding.button3,
            binding.button4,
            binding.button5,
            binding.button6
        )
    }

    private val buttonPositions by lazy {
        mapOf(
            binding.button1.id to getTtsText("kiri atas", "top left"),
            binding.button2.id to getTtsText("kiri tengah", "middle left"),
            binding.button3.id to getTtsText("kiri bawah", "bottom left"),
            binding.button4.id to getTtsText("kanan atas", "top right"),
            binding.button5.id to getTtsText("kanan tengah", "middle right"),
            binding.button6.id to getTtsText("kanan bawah", "bottom right")
        )
    }

    private var currentButtonIndex = 0
    private var quizActive = false
    private var quizRound = 0
    private val totalQuizRounds = 3
    private var currentQuizButtonId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalibrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ttsHelper = TtsHelper(this)

        val prefs = getSharedPreferences("BraillyPrefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("isCalibrated", false)) {
            startLandingActivity()
            return
        }

        buttonList.forEach { button ->
            button.setOnClickListener { handleButtonClick(button.id) }
        }

        startCalibrationIntro()
    }

    private fun handleButtonClick(buttonId: Int) {
        if (quizActive) handleQuizInput(buttonId)
        else handleCalibrationInput(buttonId)
    }

    private fun handleCalibrationInput(buttonId: Int) {
        val expectedButton = buttonList[currentButtonIndex]
        if (buttonId == expectedButton.id) {
            vibrate()
            ttsHelper?.speak(getTtsText("Sudah benar!", "Correct!"), false)
            currentButtonIndex++
            if (currentButtonIndex < buttonList.size) {
                binding.root.postDelayed({ speakNextButtonInstruction() }, 1500)
            } else {
                binding.root.postDelayed({
                    ttsHelper?.speak(
                        getTtsText(
                            "Kalibrasi dasar selesai. Sekarang kita akan melakukan quiz singkat.",
                            "Basic calibration is complete. Now we will do a short quiz."
                        ), false
                    )
                    binding.root.postDelayed({ startQuiz() }, 2000)
                }, 1500)
            }
        } else {
            vibrate()
            val num = getButtonNumber(expectedButton.id)
            val pos = buttonPositions[expectedButton.id]
            ttsHelper?.speak(
                getTtsText(
                    "Tombol salah, coba tekan tombol $num, posisinya $pos.",
                    "Wrong button, try pressing button $num at $pos."
                ), false
            )
        }
    }

    private fun speakNextButtonInstruction() {
        val button = buttonList[currentButtonIndex]
        val num = getButtonNumber(button.id)
        val pos = buttonPositions[button.id]
        ttsHelper?.speak(
            getTtsText(
                "Coba tekan tombol $num, posisinya $pos.",
                "Try pressing button $num at $pos."
            ), false
        )
    }

    private fun startQuiz() {
        quizActive = true
        quizRound = 0
        ttsHelper?.speak(getTtsText("Quiz dimulai. Tekan tombol yang saya sebutkan.", "Quiz started. Press the button I mention."), false)
        binding.root.postDelayed({ nextQuizRound() }, 3000)
    }

    private fun handleQuizInput(buttonId: Int) {
        if (buttonId == currentQuizButtonId) {
            vibrate()
            ttsHelper?.speak(getTtsText("Benar!", "Correct!"), false)
            quizRound++
            binding.root.postDelayed({ nextQuizRound() }, 2000)
        } else {
            vibrate()
            val num = getButtonNumber(buttonList.first { it.id == currentQuizButtonId }.id)
            ttsHelper?.speak(
                getTtsText(
                    "Salah, coba tekan tombol $num.",
                    "Wrong, try pressing button $num."
                ), false
            )
        }
    }

    private fun nextQuizRound() {
        if (quizRound >= totalQuizRounds) {
            ttsHelper?.speak(getTtsText("Selamat, kalibrasi selesai.", "Congratulations, calibration complete."), false)
            binding.root.postDelayed({
                getSharedPreferences("BraillyPrefs", Context.MODE_PRIVATE)
                    .edit().putBoolean("isCalibrated", true).apply()
                startLandingActivity()
            }, 3000)
            return
        }

        val button = buttonList.random()
        currentQuizButtonId = button.id
        val num = getButtonNumber(currentQuizButtonId)
        ttsHelper?.speak(getTtsText("Ronde ${quizRound + 1}: Tekan tombol $num.", "Round ${quizRound + 1}: Press button $num."), false)
    }

    private fun getButtonNumber(buttonId: Int) = buttonList.indexOfFirst { it.id == buttonId } + 1

    private fun startCalibrationIntro() {
        binding.root.postDelayed({
            ttsHelper?.speak(
                getTtsText(
                    "Selamat datang di aplikasi Beello. Sebelum menggunakan aplikasi, mohon ikuti instruksi saya untuk melakukan kalibrasi tombol.",
                    "Welcome to Beello app. Before using it, please follow my instructions to calibrate the buttons."
                ), false
            )
            binding.root.postDelayed({
                ttsHelper?.speak(
                    getTtsText(
                        "Sekarang kita mulai kalibrasi. Ikuti instruksi saya untuk setiap tombol.",
                        "Now we start calibration. Follow my instructions for each button."
                    ), false
                )
                binding.root.postDelayed({ speakNextButtonInstruction() }, 3000)
            }, 4000)
        }, 400)
    }

    override fun onDestroy() {
        ttsHelper?.stop()
        ttsHelper?.shutdown()
        super.onDestroy()
    }

    private fun startLandingActivity() {
        startActivity(Intent(this, LandingActivity::class.java))
        finish()
    }

    // --- Helper function to pick language-specific text ---
    private fun getTtsText(indonesian: String, english: String): String {
        return if (Locale.getDefault().language == "id") indonesian else english
    }
}
