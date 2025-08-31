package com.example.brailly.fragments

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.brailly.databinding.FragmentTutorial4Binding
import java.util.*

class Tutorial4Fragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentTutorial4Binding? = null
    private val binding get() = _binding!!

    private var tts: TextToSpeech? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorial4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tts = TextToSpeech(requireContext(), this)

        binding.gestureGrid.setOnClickListener {
            speakGestureGuide()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("id", "ID"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                println("Bahasa tidak didukung")
            } else {
                speakGestureGuide()
            }
        } else {
            println("Inisialisasi TTS gagal")
        }
    }

    private fun speakGestureGuide() {
        val text = """
            Kontrol aplikasi Braille.
            Geser atas untuk mengucapkan teks.
            Geser kiri untuk menghapus huruf terakhir.
            Geser kanan untuk menambah spasi.
            Geser bawah untuk menghapus semua teks.
        """.trimIndent()
        speak(text)
    }

    private fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop()
        tts?.shutdown()
        _binding = null
    }
}
