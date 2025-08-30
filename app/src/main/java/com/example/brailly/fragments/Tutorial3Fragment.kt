package com.example.brailly.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.brailly.R
import com.example.brailly.adapters.BrailleAdapter
import com.example.brailly.databinding.FragmentTutorial2Binding
import com.example.brailly.databinding.FragmentTutorial3Binding
import com.example.brailly.models.BrailleItem

class Tutorial3Fragment : Fragment() {

    private lateinit var binding: FragmentTutorial3Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTutorial3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.brailleRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4) // 4 kolom
        binding.brailleRecyclerView.adapter = BrailleAdapter(getBrailleNumberList())
    }

    private fun getBrailleNumberList(): List<BrailleItem> {
        return listOf(
            BrailleItem("1", R.drawable.braille_1),
            BrailleItem("2", R.drawable.braille_2),
            BrailleItem("3", R.drawable.braille_3),
            BrailleItem("4", R.drawable.braille_4),
            BrailleItem("5", R.drawable.braille_5),
            BrailleItem("6", R.drawable.braille_6),
            BrailleItem("7", R.drawable.braille_7),
            BrailleItem("8", R.drawable.braille_8),
            BrailleItem("9", R.drawable.braille_9),
            BrailleItem("0", R.drawable.braille_0)
        )
    }

}