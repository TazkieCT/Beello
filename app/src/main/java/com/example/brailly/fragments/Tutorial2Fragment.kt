package com.example.brailly.fragments

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.brailly.R
import com.example.brailly.adapters.BrailleAdapter
import com.example.brailly.databinding.FragmentTutorial2Binding
import com.example.brailly.models.BrailleItem
import java.util.Locale

class Tutorial2Fragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentTutorial2Binding? = null
    private val binding get() = _binding!!

    private var tts: TextToSpeech? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorial2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tts = TextToSpeech(requireContext(), this)

        binding.brailleRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.brailleRecyclerView.adapter = BrailleAdapter(getBrailleList())
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("id", "ID"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                println("Bahasa tidak didukung")
            } else {
                speak(
                    "Sekarang saatnya mengenali huruf A sampai Z. Setiap huruf memiliki pola titik braille yang berbeda untuk kamu pelajari."
                )
            }
        } else {
            println("Inisialisasi TTS gagal")
        }
    }

    private fun getBrailleList(): List<BrailleItem> {
        return listOf(
            BrailleItem("A", R.drawable.braille_a),
            BrailleItem("B", R.drawable.braille_b),
            BrailleItem("C", R.drawable.braille_c),
            BrailleItem("D", R.drawable.braille_d),
            BrailleItem("E", R.drawable.braille_e),
            BrailleItem("F", R.drawable.braille_f),
            BrailleItem("G", R.drawable.braille_g),
            BrailleItem("H", R.drawable.braille_h),
            BrailleItem("I", R.drawable.braille_i),
            BrailleItem("J", R.drawable.braille_j),
            BrailleItem("K", R.drawable.braille_k),
            BrailleItem("L", R.drawable.braille_l),
            BrailleItem("M", R.drawable.braille_m),
            BrailleItem("N", R.drawable.braille_n),
            BrailleItem("O", R.drawable.braille_o),
            BrailleItem("P", R.drawable.braille_p),
            BrailleItem("Q", R.drawable.braille_q),
            BrailleItem("R", R.drawable.braille_r),
            BrailleItem("S", R.drawable.braille_s),
            BrailleItem("T", R.drawable.braille_t),
            BrailleItem("U", R.drawable.braille_u),
            BrailleItem("V", R.drawable.braille_v),
            BrailleItem("W", R.drawable.braille_w),
            BrailleItem("X", R.drawable.braille_x),
            BrailleItem("Y", R.drawable.braille_y),
            BrailleItem("Z", R.drawable.braille_z)
        )
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
