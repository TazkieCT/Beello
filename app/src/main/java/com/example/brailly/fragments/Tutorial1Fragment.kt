package com.example.brailly.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.brailly.databinding.FragmentTutorial1Binding
import com.example.brailly.utils.TtsHelper

/**
 * Tutorial1Fragment provides the first step in the Braille tutorial.
 *
 * Features:
 * - Displays instructional content for Step 1.
 * - Text-to-Speech (TTS) reads the tutorial description aloud when tapped.
 */
class Tutorial1Fragment : Fragment() {

    // View binding instance
    private var _binding: FragmentTutorial1Binding? = null
    private val binding get() = _binding!!

    // Text-to-Speech helper instance
    private lateinit var ttsHelper: TtsHelper

    /** Inflates the fragment view using View Binding */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorial1Binding.inflate(inflater, container, false)
        return binding.root
    }

    /** Initializes TTS and sets click listener for reading tutorial text */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ttsHelper = TtsHelper(requireContext())

        binding.root.setOnClickListener {
            ttsHelper.speak(getString(com.example.brailly.R.string.tutorial_description), false)
        }

        // Speak automatically once when fragment loads
        ttsHelper.speak(getString(com.example.brailly.R.string.tutorial_description), false)
    }

    /** Cleans up TTS and binding when the view is destroyed */
    override fun onDestroyView() {
        super.onDestroyView()
        ttsHelper.stop()
        ttsHelper.shutdown()
        _binding = null
    }
}
