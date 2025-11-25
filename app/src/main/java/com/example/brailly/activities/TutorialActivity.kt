package com.example.brailly.activities

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.brailly.R
import com.example.brailly.databinding.ActivityTutorialBinding
import com.example.brailly.fragments.*
import com.example.brailly.utils.enableSwipeGestures

/**
 * TutorialActivity displays a step-by-step guide to learning Braille.
 *
 * Features:
 * - Shows tutorial text and progress bar.
 * - Loads tutorial fragments dynamically per step.
 * - Supports navigation via swipe gestures and buttons.
 */
class TutorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTutorialBinding
    private lateinit var tutorialText: TextView
    private lateinit var progressBar: ProgressBar

    private var currentStep = 0

    private val steps = listOf(
        "Langkah 1: Kenalan dengan Braille",
        "Langkah 2: Belajar huruf A - Z",
        "Langkah 3: Belajar angka 0 - 9",
//        "Langkah 4: Kontrol aplikasi Braille",
        "Langkah 4: Latihan mengetik Braille"
    )

    private val fragments = listOf(
        Tutorial1Fragment(),
        Tutorial2Fragment(),
        Tutorial3Fragment(),
        Tutorial4Fragment(),
        Tutorial5Fragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tutorialText = binding.tutorialText
        progressBar = binding.progressBar

        binding.nextButton.setOnClickListener { goNext() }
        binding.prevButton.setOnClickListener { goPrev() }

        binding.root.enableSwipeGestures(
            onSwipeLeft = { goNext() },
            onSwipeRight = { goPrev() }
        )

        updateUI()
    }

    /** Move to next step if not at the last step */
    private fun goNext() {
        if (currentStep < steps.size - 1) {
            currentStep++
            updateUI()
        }
    }

    /** Move to previous step if not at the first step */
    private fun goPrev() {
        if (currentStep > 0) {
            currentStep--
            updateUI()
        }
    }

    /** Updates tutorial text, progress, buttons, and fragment */
    private fun updateUI() {
        tutorialText.text = steps[currentStep]
        progressBar.progress = ((currentStep + 1) * 100) / steps.size

        binding.prevButton.visibility = if (currentStep == 0) View.GONE else View.VISIBLE
        binding.nextButton.visibility = if (currentStep == steps.size - 1) View.GONE else View.VISIBLE

        val fragment = fragments.getOrElse(currentStep) { Tutorial5Fragment() }

        supportFragmentManager.beginTransaction()
            .replace(R.id.tutorialContainer, fragment)
            .commit()
    }
}
