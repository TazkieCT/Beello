package com.example.brailly.activities

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import com.example.brailly.databinding.ActivityLandingBinding
import com.example.brailly.utils.enableSwipeGestures
import java.util.Locale

class LandingActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityLandingBinding
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tts = TextToSpeech(this, this)

        binding.root.enableSwipeGestures(
            onSwipeUp = {
                startActivity(Intent(this, TutorialActivity::class.java))
                speak("Membuka panduan")
            },
            onSwipeDown = {
                startActivity(Intent(this, MainActivity::class.java))
                speak("Membuka simulasi mengetik Braille")
            }
        )

        binding.brailleButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.tutorialButton.setOnClickListener {
            val intent = Intent(this, TutorialActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("id", "ID"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                println("Bahasa tidak didukung")
            } else {
                speak("Selamat datang di aplikasi Braille. Swipe ke atas untuk mulai panduan. Swipe ke bawah untuk mulai simulasi mengetik Braille.")
            }
        } else {
            println("Inisialisasi TTS gagal")
        }
    }

    private fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
