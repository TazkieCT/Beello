package com.example.brailly.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.brailly.databinding.ActivityLandingBinding
import com.example.brailly.utils.TtsHelper
import com.example.brailly.utils.enableSwipeGestures

/**
 * LandingActivity serves as the main entry point after calibration.
 *
 * Features:
 * - Swipe gestures to navigate to tutorial or Braille simulation.
 * - Buttons for direct navigation.
 * - Text-to-Speech (TTS) provides verbal instructions to guide the user.
 */
class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding
    private lateinit var ttsHelper: TtsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ttsHelper = TtsHelper(this)
        speakWelcomeInstructions()

        // Enable swipe gestures
        binding.root.enableSwipeGestures(
            onSwipeUp = {
                startTutorial()
            },
            onSwipeDown = {
                startBrailleSimulation()
            }
        )

        // Direct button navigation
        binding.brailleButton.setOnClickListener { startBrailleSimulation() }
        binding.tutorialButton.setOnClickListener { startTutorial() }
    }

    /** Provides welcome instructions using TTS */
    private fun speakWelcomeInstructions() {
        ttsHelper.speak(
            "Selamat datang di aplikasi Braille. " +
                    "Swipe ke atas untuk mulai panduan. " +
                    "Swipe ke bawah untuk mulai simulasi mengetik Braille.",
            false
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
}
