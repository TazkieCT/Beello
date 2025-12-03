package com.example.brailly.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.brailly.activities.MainActivity
import com.example.brailly.activities.QuizActivity
import com.example.brailly.databinding.FragmentTutorial5Binding
import com.example.brailly.utils.TtsHelper
import com.example.brailly.utils.enableSwipeGestures
import java.util.*

/**
 * Tutorial5Fragment provides the final step of the tutorial where users can practice Braille typing
 * through an interactive quiz or continue to the free typing simulation.
 *
 * Features:
 * - Start a Braille quiz via button or swipe gesture.
 * - Navigate to free typing simulation via swipe gesture.
 * - Text-to-Speech guidance for instructions.
 */
class Tutorial5Fragment : Fragment() {

    private lateinit var binding: FragmentTutorial5Binding
    private lateinit var ttsHelper: TtsHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTutorial5Binding.inflate(inflater, container, false)

        ttsHelper = TtsHelper(requireContext())

        val indonesianText = "Ayo berlatih mengetik huruf Braille dengan permainan quiz seru! " +
                "Geser layar ke atas atau tekan tombol Mulai Quiz untuk memulai. " +
                "Atau geser ke bawah untuk simulasi mengetik Braille dengan bebas."
        val englishText = "Let's practice typing Braille letters with a fun quiz game! " +
                "Swipe up or press Start Quiz to begin, or swipe down for free Braille typing simulation."

        ttsHelper.speak(getTtsText(indonesianText, englishText), false)

        binding.startQuizButton.setOnClickListener {
            goToQuiz()
        }

        binding.root.enableSwipeGestures(
            onSwipeUp = { goToQuiz() },
            onSwipeDown = {
                ttsHelper.stop()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                ttsHelper.speak(
                    getTtsText(
                        "Membuka simulasi mengetik Braille",
                        "Opening free Braille typing simulation"
                    ),
                    false
                )
            }
        )

        return binding.root
    }

    private fun goToQuiz() {
        ttsHelper.stop()
        startActivity(Intent(requireContext(), QuizActivity::class.java))
        ttsHelper.speak(getTtsText("Memulai quiz Braille", "Starting Braille quiz"), false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ttsHelper.stop()
        ttsHelper.shutdown()
    }

    /** Helper function to choose text based on phone language */
    private fun getTtsText(indonesian: String, english: String): String {
        return if (Locale.getDefault().language == "id") indonesian else english
    }
}
