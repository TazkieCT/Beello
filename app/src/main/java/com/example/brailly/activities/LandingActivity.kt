package com.example.brailly.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.brailly.databinding.ActivityLandingBinding
import com.example.brailly.utils.TtsHelper
import com.example.brailly.utils.enableSwipeGestures
import java.util.*

class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding
    private lateinit var ttsHelper: TtsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize TTS helper
        ttsHelper = TtsHelper(this)

        // Speak welcome instructions
        speakWelcomeInstructions()

        // Enable swipe gestures
        binding.root.enableSwipeGestures(
            onSwipeUp = { startTutorial() },
            onSwipeDown = { startBrailleSimulation() }
        )

        // Direct button navigation
        binding.brailleButton.setOnClickListener { startBrailleSimulation() }
        binding.tutorialButton.setOnClickListener { startTutorial() }
    }

    /** Provides welcome instructions using TTS */
    private fun speakWelcomeInstructions() {
        ttsHelper.speak(
            getTtsText(
                "Selamat datang di aplikasi Braille. Swipe ke atas untuk mulai panduan. Swipe ke bawah untuk mulai simulasi mengetik Braille.",
                "Welcome to the Braille app. Swipe up to start the tutorial. Swipe down to start the Braille typing simulation."
            ),
            flush = true
        )
    }

    /** Launches TutorialActivity */
    private fun startTutorial() {
        ttsHelper.stop()
        startActivity(Intent(this, TutorialActivity::class.java))
    }

    /** Launches MainActivity (Braille simulation) */
    private fun startBrailleSimulation() {
        ttsHelper.stop()
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onDestroy() {
        ttsHelper.stop()
        ttsHelper.shutdown()
        super.onDestroy()
    }

    // --- Helper function to pick language-specific text ---
    private fun getTtsText(indonesian: String, english: String): String {
        return if (Locale.getDefault().language == "id") indonesian else english
    }
}
