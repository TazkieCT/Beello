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
import java.util.*

class TutorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTutorialBinding
    private lateinit var tutorialText: TextView
    private lateinit var progressBar: ProgressBar

    private var currentStep = 0

    // --- Tutorial steps in Indonesian and English ---
    private val stepsIndonesian = listOf(
        "Langkah 1: Kenalan dengan Braille",
        "Langkah 2: Belajar huruf A - Z",
        "Langkah 3: Belajar angka 0 - 9",
        "Langkah 4: Latihan mengetik Braille"
    )

    private val stepsEnglish = listOf(
        "Step 1: Get to know Braille",
        "Step 2: Learn letters A - Z",
        "Step 3: Learn numbers 0 - 9",
        "Step 4: Braille typing practice"
    )

    private val fragments = listOf(
        Tutorial1Fragment(),
        Tutorial2Fragment(),
        Tutorial3Fragment(),
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

    private fun goNext() {
        if (currentStep < stepsIndonesian.size - 1) {
            currentStep++
            updateUI()
        }
    }

    private fun goPrev() {
        if (currentStep > 0) {
            currentStep--
            updateUI()
        }
    }

    private fun updateUI() {
        tutorialText.text = getTextByLanguage(
            stepsIndonesian[currentStep],
            stepsEnglish[currentStep]
        )
        progressBar.progress = ((currentStep + 1) * 100) / stepsIndonesian.size

        binding.prevButton.visibility = if (currentStep == 0) View.GONE else View.VISIBLE
        binding.nextButton.visibility = if (currentStep == stepsIndonesian.size - 1) View.GONE else View.VISIBLE

        val fragment = fragments.getOrElse(currentStep) { Tutorial5Fragment() }

        supportFragmentManager.beginTransaction()
            .replace(R.id.tutorialContainer, fragment)
            .commit()
    }

    /** Helper function to choose text based on phone language */
    private fun getTextByLanguage(indonesian: String, english: String): String {
        return if (Locale.getDefault().language == "id") indonesian else english
    }
}
