package com.example.brailly.fragments

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.brailly.activities.MainActivity
import com.example.brailly.activities.QuizActivity
import com.example.brailly.activities.TutorialActivity
import com.example.brailly.databinding.FragmentTutorial5Binding
import com.example.brailly.utils.enableSwipeGestures
import java.util.*

class Tutorial5Fragment : Fragment(), TextToSpeech.OnInitListener {

    private lateinit var binding: FragmentTutorial5Binding
    private var tts: TextToSpeech? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTutorial5Binding.inflate(inflater, container, false)

        tts = TextToSpeech(requireContext(), this)

        binding.startQuizButton.setOnClickListener {
            goToQuiz()
        }

        binding.root.enableSwipeGestures(
            onSwipeUp = {
                startActivity(Intent(requireContext(), QuizActivity::class.java))
                speak("Memulai quiz Braille")
            },
            onSwipeDown = {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                speak("Membuka simulasi mengetik Braille")
            }
        )

        return binding.root
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("id", "ID")
            speak(
                "Ayo berlatih mengetik huruf Braille dengan permainan quiz seru! " +
                        "Geser layar ke atas atau tekan tombol Mulai Quiz untuk memulai. " +
                        "Atau geser ke bawah untuk simulasi mengetik Braille dengan bebas."
            )
        }
    }

    private fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun goToQuiz() {
        val intent = Intent(requireContext(), QuizActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
