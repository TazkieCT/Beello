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

    /** Binding for layout views */
    private lateinit var binding: ActivityCalibrationBinding

    /** TTS helper object, nullable for safety */
    private var ttsHelper: TtsHelper? = null

    /** List of Braille buttons */
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

    /** Map of button ID to their respective positions in Indonesian */
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

    /** Index for current button during calibration */
    private var currentButtonIndex = 0

    /** Flag for quiz mode */
    private var quizActive = false

    /** Current quiz round number */
    private var quizRound = 0

    /** Total number of quiz rounds */
    private val totalQuizRounds = 3

    /** Button ID for the current quiz round */
    private var currentQuizButtonId = 0

    /**
     * Initializes the activity, checks calibration state, and starts calibration if needed.
     */
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

    /**
     * Handles button clicks depending on the current phase:
     * calibration or quiz.
     */
    private fun handleButtonClick(buttonId: Int) {
        if (quizActive) handleQuizInput(buttonId)
        else handleCalibrationInput(buttonId)
    }

    /**
     * Handles input during the calibration phase.
     * Checks if the pressed button matches the expected one and provides TTS feedback.
     */
    private fun handleCalibrationInput(buttonId: Int) {
        val expectedButton = buttonList[currentButtonIndex]
        if (buttonId == expectedButton.id) {
            vibrate()
            ttsHelper?.speak("Sudah benar!", false)
            currentButtonIndex++
            if (currentButtonIndex < buttonList.size) {
                binding.root.postDelayed({ speakNextButtonInstruction() }, 1500)
            } else {
                binding.root.postDelayed({
                    ttsHelper?.speak(
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
            ttsHelper?.speak("Tombol salah, coba tekan tombol $num, posisinya $pos.", false)
        }
    }

    /** Speaks instruction for the next calibration button */
    private fun speakNextButtonInstruction() {
        val button = buttonList[currentButtonIndex]
        val num = getButtonNumber(button.id)
        val pos = buttonPositions[button.id]
        ttsHelper?.speak("Coba tekan tombol $num, posisinya $pos.", false)
    }

    /** Starts the quiz phase after calibration is complete */
    private fun startQuiz() {
        quizActive = true
        quizRound = 0
        ttsHelper?.speak("Quiz dimulai. Tekan tombol yang saya sebutkan.", false)
        binding.root.postDelayed({ nextQuizRound() }, 3000)
    }

    /**
     * Handles input during the quiz phase.
     * Provides TTS feedback for correct or incorrect answers.
     */
    private fun handleQuizInput(buttonId: Int) {
        if (buttonId == currentQuizButtonId) {
            vibrate()
            ttsHelper?.speak("Benar!", false)
            quizRound++
            binding.root.postDelayed({ nextQuizRound() }, 2000)
        } else {
            vibrate()
            val num = getButtonNumber(buttonList.first { it.id == currentQuizButtonId }.id)
            ttsHelper?.speak("Salah, coba tekan tombol $num.", false)
        }
    }

    /**
     * Proceeds to the next quiz round or finishes calibration if all rounds are complete.
     */
    private fun nextQuizRound() {
        if (quizRound >= totalQuizRounds) {
            ttsHelper?.speak("Selamat, kalibrasi selesai.", false)
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
        ttsHelper?.speak("Ronde ${quizRound + 1}: Tekan tombol $num.", false)
    }

    /** Maps button ID to its number for TTS feedback */
    private fun getButtonNumber(buttonId: Int) = buttonList.indexOfFirst { it.id == buttonId } + 1

    /** Starts the calibration introduction with TTS instructions */
    private fun startCalibrationIntro() {
        binding.root.postDelayed({
            ttsHelper?.speak(
                "Selamat datang di aplikasi Beello. Sebelum menggunakan aplikasi, mohon ikuti instruksi saya untuk melakukan kalibrasi tombol.",
                false
            )
            binding.root.postDelayed({
                ttsHelper?.speak(
                    "Sekarang kita mulai kalibrasi. Ikuti instruksi saya untuk setiap tombol.",
                    false
                )
                binding.root.postDelayed({ speakNextButtonInstruction() }, 3000)
            }, 4000)
        }, 400)
    }

    /** Stops and shuts down TTS safely when activity is destroyed */
    override fun onDestroy() {
        ttsHelper?.stop()
        ttsHelper?.shutdown()
        super.onDestroy()
    }

    /** Launches the landing activity after calibration */
    private fun startLandingActivity() {
        startActivity(Intent(this, LandingActivity::class.java))
        finish()
    }
}
