package com.example.brailly.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.brailly.adapters.BrailleAdapter
import com.example.brailly.databinding.FragmentTutorial3Binding
import com.example.brailly.utils.BrailleData
import com.example.brailly.utils.TtsHelper
import java.util.*

/**
 * Tutorial3Fragment introduces Braille numbers (0-9) to the user.
 *
 * Features:
 * - Displays Braille numbers in a grid using RecyclerView.
 * - Provides visual learning for numeric Braille patterns.
 * - Uses Text-to-Speech (TTS) to guide the user in learning numbers.
 */
class Tutorial3Fragment : Fragment() {

    private var _binding: FragmentTutorial3Binding? = null
    private val binding get() = _binding!!
    private lateinit var ttsHelper: TtsHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorial3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ttsHelper = TtsHelper(requireContext())

        val indonesianText = "Sekarang mari kita pelajari angka Braille dari 0 sampai 9. " +
                "Setiap angka memiliki pola titik Braille masing-masing, sama seperti huruf Braille a sampai j"
        val englishText = "Now let's learn Braille numbers from 0 to 9. " +
                "Each number has its own Braille dot pattern, similar to letters a through j."

        ttsHelper.speak(getTtsText(indonesianText, englishText), false)

        binding.brailleRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.brailleRecyclerView.adapter = BrailleAdapter(BrailleData.numbers)
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
