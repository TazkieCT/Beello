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
import com.example.brailly.models.BrailleItem

class Tutorial2Fragment : Fragment() {

    private lateinit var binding: FragmentTutorial2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTutorial2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.brailleRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.brailleRecyclerView.adapter = BrailleAdapter(getBrailleList())
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
}
