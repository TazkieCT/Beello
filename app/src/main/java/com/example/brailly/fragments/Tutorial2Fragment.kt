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

        binding.brailleRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4) // 4 kolom
        binding.brailleRecyclerView.adapter = BrailleAdapter(getBrailleList())
    }

    private fun getBrailleList(): List<BrailleItem> {
        return listOf(
            BrailleItem("A", R.drawable.a),
            BrailleItem("B", R.drawable.b),
            BrailleItem("C", R.drawable.c),
            BrailleItem("D", R.drawable.d),
            BrailleItem("E", R.drawable.e),
            BrailleItem("F", R.drawable.f),
            BrailleItem("G", R.drawable.g),
            BrailleItem("H", R.drawable.h),
            BrailleItem("I", R.drawable.i),
            BrailleItem("J", R.drawable.j),
            BrailleItem("K", R.drawable.k),
            BrailleItem("L", R.drawable.l),
            BrailleItem("M", R.drawable.m),
            BrailleItem("N", R.drawable.n),
            BrailleItem("O", R.drawable.o),
            BrailleItem("P", R.drawable.p),
            BrailleItem("Q", R.drawable.q),
            BrailleItem("R", R.drawable.r),
            BrailleItem("S", R.drawable.s),
            BrailleItem("T", R.drawable.t),
            BrailleItem("U", R.drawable.u),
            BrailleItem("V", R.drawable.v),
            BrailleItem("W", R.drawable.w),
            BrailleItem("X", R.drawable.x),
            BrailleItem("Y", R.drawable.y),
            BrailleItem("Z", R.drawable.z)
        )
    }
}
