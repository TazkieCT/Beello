package com.example.brailly.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.brailly.R
import com.example.brailly.models.BrailleItem

/**
 * RecyclerView adapter for displaying Braille images.
 *
 * @param brailleList The list of Braille items to display.
 */
class BrailleAdapter(private val brailleList: List<BrailleItem>) :
    RecyclerView.Adapter<BrailleAdapter.BrailleViewHolder>() {

    /**
     * ViewHolder for Braille items.
     *
     * @param itemView The view representing a single Braille item.
     */
    class BrailleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.brailleImage)
    }

    /** Inflates the item view and returns a new ViewHolder */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrailleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_braille, parent, false)
        return BrailleViewHolder(view)
    }

    /** Binds the Braille image resource to the ImageView in the ViewHolder */
    override fun onBindViewHolder(holder: BrailleViewHolder, position: Int) {
        val item = brailleList[position]
        holder.image.setImageResource(item.imageRes)
    }

    /** Returns the total number of Braille items */
    override fun getItemCount(): Int = brailleList.size
}
