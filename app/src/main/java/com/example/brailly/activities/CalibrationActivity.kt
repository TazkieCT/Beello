package com.example.brailly.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.brailly.databinding.ActivityCalibrationBinding
import com.example.brailly.helper.vibrate
import com.example.brailly.utils.TtsHelper

/**
 * CalibrationActivity guides the user through button calibration before using the app.
 *
 * Features:
 * - Step-by-step button calibration with TTS instructions.
 * - Short quiz to verify button positions.
 * - Tactile feedback using device vibration.
 * - Persists calibration state in SharedPreferences to skip future calibration.
 */
class CalibrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalibrationBinding
    private lateinit var ttsHelper: TtsHelper

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
            binding.button1.id to "kiri atas",
            binding.button2.id to "kiri tengah",
            binding.button3.id to "kiri bawah",
            binding.button4.id to "kanan atas",
            binding.button5.id to "kanan tengah",
            binding.button6.id to "kanan bawah"
        )
    }

    private var currentButtonIndex = 0
    private var quizActive = false
    private var quizRound = 0
    private val totalQuizRounds = 3
    private var currentQuizButtonId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("BraillyPrefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("isCalibrated", false)) {
            startLandingActivity()
            return
        }

        binding = ActivityCalibrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ttsHelper = TtsHelper(this)

        buttonList.forEach { button ->
            button.setOnClickListener { handleButtonClick(button.id) }
        }

        startCalibrationIntro()
    }

    /** Handles button click depending on phase (calibration or quiz) */
    private fun handleButtonClick(buttonId: Int) {
        if (quizActive) handleQuizInput(buttonId)
        else handleCalibrationInput(buttonId)
    }

    /** Calibration input handling */
    private fun handleCalibrationInput(buttonId: Int) {
        val expectedButton = buttonList[currentButtonIndex]
        if (buttonId == expectedButton.id) {
            vibrate()
            ttsHelper.speak("Sudah benar!", false)
            currentButtonIndex++
            if (currentButtonIndex < buttonList.size) {
                binding.root.postDelayed({ speakNextButtonInstruction() }, 1500)
            } else {
                binding.root.postDelayed({
                    ttsHelper.speak(
                        "Kalibrasi dasar selesai. Sekarang kita akan melakukan quiz singkat.",
                        false
                    )
                    binding.root.postDelayed({ startQuiz() }, 2000)
                }, 1500)
            }
        } else {
            vibrate()
            val num = getButtonNumber(expectedButton.id)
            val pos = buttonPositions[expectedButton.id]
            ttsHelper.speak("Tombol salah, coba tekan tombol $num, posisinya $pos.", false)
        }
    }

    /** Speaks instruction for the next calibration button */
    private fun speakNextButtonInstruction() {
        val button = buttonList[currentButtonIndex]
        val num = getButtonNumber(button.id)
        val pos = buttonPositions[button.id]
        ttsHelper.speak("Coba tekan tombol $num, posisinya $pos.", false)
    }

    /** Starts the quiz phase */
    private fun startQuiz() {
        quizActive = true
        quizRound = 0
        ttsHelper.speak("Quiz dimulai. Tekan tombol yang saya sebutkan.", false)
        binding.root.postDelayed({ nextQuizRound() }, 3000)
    }

    /** Handles quiz input */
    private fun handleQuizInput(buttonId: Int) {
        if (buttonId == currentQuizButtonId) {
            vibrate()
            ttsHelper.speak("Benar!", false)
            quizRound++
            binding.root.postDelayed({ nextQuizRound() }, 2000)
        } else {
            vibrate()
            val num = getButtonNumber(buttonList.first { it.id == currentQuizButtonId }.id)
            ttsHelper.speak("Salah, coba tekan tombol $num.", false)
        }
    }

    /** Proceeds to next quiz round or finishes calibration */
    private fun nextQuizRound() {
        if (quizRound >= totalQuizRounds) {
            ttsHelper.speak("Selamat, kalibrasi selesai.", false)
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
        ttsHelper.speak("Ronde ${quizRound + 1}: Tekan tombol $num.", false)
    }

    /** Maps button ID to number */
    private fun getButtonNumber(buttonId: Int) = buttonList.indexOfFirst { it.id == buttonId } + 1

    /** Starts calibration introduction */
    private fun startCalibrationIntro() {
        binding.root.postDelayed({
            ttsHelper.speak(
                "Selamat datang di aplikasi Beello. Sebelum menggunakan aplikasi, mohon ikuti instruksi saya untuk melakukan kalibrasi tombol.",
                false
            )
            binding.root.postDelayed({
                ttsHelper.speak(
                    "Sekarang kita mulai kalibrasi. Ikuti instruksi saya untuk setiap tombol.",
                    false
                )
                binding.root.postDelayed({ speakNextButtonInstruction() }, 3000)
            }, 4000)
        }, 400)
    }

    override fun onDestroy() {
        ttsHelper.stop()
        ttsHelper.shutdown()
        super.onDestroy()
    }

    /** Launches the main landing activity */
    private fun startLandingActivity() {
        startActivity(Intent(this, LandingActivity::class.java))
        finish()
    }
}
