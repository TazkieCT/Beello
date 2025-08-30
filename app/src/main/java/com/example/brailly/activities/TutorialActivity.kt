package com.example.brailly.activities

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.brailly.R
import com.example.brailly.databinding.ActivityTutorialBinding
import com.example.brailly.fragments.Tutorial1Fragment
import com.example.brailly.fragments.Tutorial2Fragment
import com.example.brailly.fragments.Tutorial3Fragment
import com.example.brailly.fragments.Tutorial4Fragment
import com.example.brailly.fragments.Tutorial5Fragment

class TutorialActivity : AppCompatActivity() {

    private lateinit var tutorialText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var binding: ActivityTutorialBinding

    private val steps = listOf(
        "Langkah 1: Kenalan dengan Braille",
        "Langkah 2: Belajar huruf A - Z",
        "Langkah 3: Belajar angka 0 - 9",
        "Langkah 4: Kontrol aplikasi Braille",
        "Langkah 5: Latihan mengetik Braille",
    )
    private var currentStep = 0

    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tutorialText = binding.tutorialText
        progressBar = binding.progressBar

        val nextButton = binding.nextButton
        val prevButton = binding.prevButton

        updateUI()

        nextButton.setOnClickListener { goNext() }
        prevButton.setOnClickListener { goPrev() }

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 100
            private val SWIPE_VELOCITY_THRESHOLD = 100

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null || e2 == null) return false
                val diffX = e2.x - e1.x
                val diffY = e2.y - e1.y
                return if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            goPrev()
                        } else {
                            goNext()
                        }
                        true
                    } else false
                } else false
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    private fun goNext() {
        if (currentStep < steps.size - 1) {
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
        tutorialText.text = steps[currentStep]
        progressBar.progress = ((currentStep + 1) * 100) / steps.size

        binding.prevButton.visibility = if (currentStep == 0) View.GONE else View.VISIBLE
        binding.nextButton.visibility = if (currentStep == steps.size - 1) View.GONE else View.VISIBLE

        val fragment = when (currentStep) {
            0 -> Tutorial1Fragment()
            1 -> Tutorial2Fragment()
            2 -> Tutorial3Fragment()
            3 -> Tutorial4Fragment()
            4 -> Tutorial5Fragment()
            else -> Tutorial5Fragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.tutorialContainer, fragment)
            .commit()
    }

}
