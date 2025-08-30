package com.example.brailly.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brailly.R
import com.example.brailly.models.BrailleItem

class BrailleAdapter(private val brailleList: List<BrailleItem>) :
    RecyclerView.Adapter<BrailleAdapter.BrailleViewHolder>() {

    class BrailleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.brailleImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrailleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_braille, parent, false)
        return BrailleViewHolder(view)
    }

    override fun onBindViewHolder(holder: BrailleViewHolder, position: Int) {
        val item = brailleList[position]
        holder.image.setImageResource(item.imageRes)
    }

    override fun getItemCount() = brailleList.size
}
