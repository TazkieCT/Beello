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
import java.util.*

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorial2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Text-to-Speech
        ttsHelper = TtsHelper(requireContext())

        val indonesianText = "Sekarang saatnya belajar huruf A hingga Z. Setiap huruf memiliki pola titik Braille unik untuk dipelajari."
        val englishText = "Now it's time to learn the letters A to Z. Each letter has a unique Braille dot pattern for you to study."

        // Speak instructions based on phone language
        ttsHelper.speak(getTtsText(indonesianText, englishText), false)

        // Set up RecyclerView with a 4-column grid
        binding.brailleRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.brailleRecyclerView.adapter = BrailleAdapter(BrailleData.letters)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ttsHelper.stop()
        ttsHelper.shutdown()
        _binding = null
    }

    /** Helper function to choose text based on phone language */
    private fun getTtsText(indonesian: String, english: String): String {
        return if (Locale.getDefault().language == "id") indonesian else english
    }
}
