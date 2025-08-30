package com.example.brailly.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.brailly.R
import com.example.brailly.activities.QuizActivity
import com.example.brailly.databinding.FragmentTutorial5Binding

class Tutorial5Fragment : Fragment() {

    private lateinit var binding: FragmentTutorial5Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTutorial5Binding.inflate(inflater, container, false)

        val btnStartQuiz: Button = binding.btnStartQuiz
        btnStartQuiz.setOnClickListener {
            val intent = Intent(requireContext(), QuizActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

}