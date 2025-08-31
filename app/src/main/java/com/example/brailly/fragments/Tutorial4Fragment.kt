package com.example.brailly.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.brailly.databinding.FragmentTutorial4Binding
import com.example.brailly.utils.TtsHelper

/**
 * Tutorial4Fragment introduces gesture controls in the Braille app.
 *
 * Features:
 * - Explains swipe gestures for text manipulation.
 * - Uses Text-to-Speech (TTS) to guide the user.
 */
class Tutorial4Fragment : Fragment() {

    // View binding instance
    private var _binding: FragmentTutorial4Binding? = null
    private val binding get() = _binding!!

    // Text-to-Speech helper instance
    private lateinit var ttsHelper: TtsHelper

    /** Inflates the fragment layout using View Binding */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorial4Binding.inflate(inflater, container, false)
        return binding.root
    }

    /** Initializes TTS and sets up click listener for gesture guide */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ttsHelper = TtsHelper(requireContext())

        binding.gestureGrid.setOnClickListener {
            speakGestureGuide()
        }

        // Speak instructions on view creation
        speakGestureGuide()
    }

    /** Speaks gesture guide instructions */
    private fun speakGestureGuide() {
        val text = """
            Kontrol aplikasi Braille.
            Geser atas untuk mengucapkan teks.
            Geser kiri untuk menghapus huruf terakhir.
            Geser kanan untuk menambah spasi.
            Geser bawah untuk menghapus semua teks.
        """.trimIndent()
        ttsHelper.speak(text, false)
    }

    /** Cleans up TTS and binding when the view is destroyed */
    override fun onDestroyView() {
        super.onDestroyView()
        ttsHelper.stop()
        ttsHelper.shutdown()
        _binding = null
    }
}
