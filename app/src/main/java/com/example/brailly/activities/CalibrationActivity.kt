package com.example.brailly.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import com.example.brailly.databinding.ActivityCalibrationBinding
import com.example.brailly.helper.vibrate
import java.util.*

/**
 * CalibrationActivity guides the user through button calibration before using the app.
 *
 * Features:
 * - Step-by-step button calibration with TTS instructions.
 * - Short quiz to verify button positions.
 * - Tactile feedback using device vibration.
 * - Persists calibration state in SharedPreferences to skip future calibration.
 */
class CalibrationActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityCalibrationBinding
    private lateinit var tts: TextToSpeech

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

    private lateinit var buttonPositions: Map<Int, String>
    private var currentButtonIndex = 0
    private var quizActive = false

    // Quiz state
    private var quizRound = 0
    private val totalQuizRounds = 3
    private var currentQuizButtonId: Int = 0

    // TTS readiness
    private var ttsReady = false
    private val speakQueue = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("BraillyPrefs", Context.MODE_PRIVATE)
        val isCalibrated = prefs.getBoolean("isCalibrated", false)
        if (isCalibrated) {
            startLandingActivity()
            finish()
            return
        }

        binding = ActivityCalibrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tts = TextToSpeech(this, this)

        buttonPositions = mapOf(
            binding.button1.id to "kiri atas",
            binding.button2.id to "kiri tengah",
            binding.button3.id to "kiri bawah",
            binding.button4.id to "kanan atas",
            binding.button5.id to "kanan tengah",
            binding.button6.id to "kanan bawah"
        )

        buttonList.forEach { button ->
            button.setOnClickListener {
                if (!quizActive) {
                    handleCalibrationInput(button.id)
                } else {
                    handleQuizInput(button.id)
                }
            }
        }
    }

    /** Handles user input during the calibration phase */
    private fun handleCalibrationInput(buttonId: Int) {
        if (buttonId == buttonList[currentButtonIndex].id) {
            vibrate()
            speak("Sudah benar!")
            currentButtonIndex++
            if (currentButtonIndex < buttonList.size) {
                ttsPlayDelayed(1500) { speakNextButtonInstruction() }
            } else {
                ttsPlayDelayed(1500) {
                    speak("Kalibrasi dasar selesai. Sekarang kita akan melakukan quiz singkat.")
                    ttsPlayDelayed(2000) { startQuiz() }
                }
            }
        } else {
            vibrate()
            val correctButton = buttonList[currentButtonIndex]
            val num = getButtonNumber(correctButton.id)
            val pos = buttonPositions[correctButton.id]
            speak("Tombol salah, coba tekan tombol $num, posisinya $pos.")
        }
    }

    /** Speaks instruction for the next button to press */
    private fun speakNextButtonInstruction() {
        val button = buttonList[currentButtonIndex]
        val num = getButtonNumber(button.id)
        val pos = buttonPositions[button.id]
        speak("Coba tekan tombol $num, posisinya $pos.")
    }

    /** Starts the quiz phase after calibration */
    private fun startQuiz() {
        quizActive = true
        quizRound = 0
        speak("Quiz dimulai. Tekan tombol yang saya sebutkan.")
        ttsPlayDelayed(3000) { nextQuizRound() }
    }

    /** Advances to the next quiz round or completes calibration */
    private fun nextQuizRound() {
        if (quizRound >= totalQuizRounds) {
            speak("Selamat, kalibrasi selesai.")
            ttsPlayDelayed(3000) {
                getSharedPreferences("BraillyPrefs", Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("isCalibrated", true)
                    .apply()
                startLandingActivity()
            }
            return
        }

        val button = buttonList.random()
        currentQuizButtonId = button.id
        val num = getButtonNumber(currentQuizButtonId)
        speak("Ronde ${quizRound + 1}: Tekan tombol $num.")
    }

    /** Handles user input during the quiz phase */
    private fun handleQuizInput(buttonId: Int) {
        if (buttonId == currentQuizButtonId) {
            vibrate()
            speak("Benar!")
            quizRound++
            ttsPlayDelayed(2000) { nextQuizRound() }
        } else {
            vibrate()
            val correctButton = buttonList.first { it.id == currentQuizButtonId }
            val num = getButtonNumber(correctButton.id)
            speak("Salah, coba tekan tombol $num.")
        }
    }

    /** Returns the numeric identifier for a given button */
    private fun getButtonNumber(buttonId: Int) = when (buttonId) {
        binding.button1.id -> 1
        binding.button2.id -> 2
        binding.button3.id -> 3
        binding.button4.id -> 4
        binding.button5.id -> 5
        binding.button6.id -> 6
        else -> 0
    }

    /** Speaks text using TTS, queues if TTS is not ready */
    private fun speak(text: String) {
        if (ttsReady) {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, null)
        } else {
            speakQueue.add(text)
        }
    }

    /** Executes a given action after a delay on the UI thread */
    private fun ttsPlayDelayed(delay: Long, action: () -> Unit) {
        binding.root.postDelayed(action, delay)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale("id", "ID")
            ttsReady = true
            speakQueue.forEach { speak(it) }
            speakQueue.clear()

            // Intro and calibration instructions
            ttsPlayDelayed(400) {
                speak("Selamat datang di aplikasi Beello. Sebelum menggunakan aplikasi, mohon ikuti instruksi saya untuk melakukan kalibrasi tombol.")
                ttsPlayDelayed(4000) {
                    speak("Sekarang kita mulai kalibrasi. Ikuti instruksi saya untuk setiap tombol.")
                    ttsPlayDelayed(3000) { speakNextButtonInstruction() }
                }
            }
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }

    /** Launches the main landing activity after calibration */
    private fun startLandingActivity() {
        startActivity(Intent(this, LandingActivity::class.java))
        finish()
    }
}
