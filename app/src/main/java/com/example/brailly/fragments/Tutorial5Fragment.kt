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

    // View binding instance
    private lateinit var binding: FragmentTutorial5Binding

    // Text-to-Speech helper instance
    private lateinit var ttsHelper: TtsHelper

    /** Inflates the fragment layout using View Binding */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTutorial5Binding.inflate(inflater, container, false)

        ttsHelper = TtsHelper(requireContext())

        // Speak instructions on view creation
        ttsHelper.speak(
            "Ayo berlatih mengetik huruf Braille dengan permainan quiz seru! " +
                    "Geser layar ke atas atau tekan tombol Mulai Quiz untuk memulai. " +
                    "Atau geser ke bawah untuk simulasi mengetik Braille dengan bebas.",
            false
        )

        binding.startQuizButton.setOnClickListener {
            goToQuiz()
        }

        binding.root.enableSwipeGestures(
            onSwipeUp = { goToQuiz() },
            onSwipeDown = {
                ttsHelper.stop()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                ttsHelper.speak("Membuka simulasi mengetik Braille", false)
            }
        )

        return binding.root
    }

    /** Stops TTS and navigates to the QuizActivity */
    private fun goToQuiz() {
        ttsHelper.stop()
        startActivity(Intent(requireContext(), QuizActivity::class.java))
        ttsHelper.speak("Memulai quiz Braille", false)
    }

    /** Cleans up TTS when the fragment is destroyed */
    override fun onDestroyView() {
        super.onDestroyView()
        ttsHelper.stop()
        ttsHelper.shutdown()
    }
}
