package com.example.brailly.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.brailly.adapters.BrailleAdapter
import com.example.brailly.databinding.FragmentTutorial2Binding
import com.example.brailly.utils.BrailleData
import com.example.brailly.utils.TtsHelper

/**
 * Fragment to display Braille letters from A to Z.
 *
 * Features:
 * - Shows Braille letters in a grid using RecyclerView.
 * - Provides audio guidance using Text-to-Speech (TTS).
 */
class Tutorial2Fragment : Fragment() {

    private var _binding: FragmentTutorial2Binding? = null
    private val binding get() = _binding!!

    private lateinit var ttsHelper: TtsHelper

    /**
     * Inflates the fragment layout using View Binding.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorial2Binding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Initializes TTS and sets up the RecyclerView with Braille letters.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Text-to-Speech
        ttsHelper = TtsHelper(requireContext())
        ttsHelper.speak(
            "Now it's time to learn the letters A to Z. " +
                    "Each letter has a unique Braille dot pattern for you to study.",
            false
        )

        // Set up RecyclerView with a 4-column grid
        binding.brailleRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.brailleRecyclerView.adapter = BrailleAdapter(BrailleData.letters)
    }

    /**
     * Cleans up TTS and binding when the view is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        ttsHelper.stop()
        ttsHelper.shutdown()
        _binding = null
    }
}
