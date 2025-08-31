package com.example.brailly.models

/**
 * Represents a Braille item for display in RecyclerView or other UI components.
 *
 * @param letter The character or symbol represented by this Braille item.
 * @param imageRes The drawable resource ID representing the Braille dots pattern.
 */
data class BrailleItem(
    val letter: String,
    val imageRes: Int
)
