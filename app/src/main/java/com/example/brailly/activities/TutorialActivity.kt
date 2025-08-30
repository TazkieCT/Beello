package com.example.brailly.activities

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.brailly.R
import com.example.brailly.databinding.ActivityTutorialBinding

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

        binding.prevButton.visibility = if (currentStep == 0) {
            android.view.View.GONE
        } else {
            android.view.View.VISIBLE
        }

        binding.nextButton.visibility = if (currentStep == steps.size - 1) {
            android.view.View.GONE
        } else {
            android.view.View.VISIBLE
        }

        val container = binding.tutorialContainer
        container.removeAllViews()

        val layoutRes = when (currentStep) {
            0 -> R.layout.fragment_tutorial1
            1 -> R.layout.fragment_tutorial2
            2 -> R.layout.fragment_tutorial1
            3 -> R.layout.fragment_tutorial1
            4 -> R.layout.fragment_tutorial5
            else -> R.layout.fragment_tutorial5
        }

        val view = layoutInflater.inflate(layoutRes, container, false)
        container.addView(view)
    }
}
