package com.example.brailly.utils

object BrailleMappings {

    // Maps 6-dot Braille patterns to letters A-Z
    val letterMap = mapOf(
        "100000" to "A", "110000" to "B", "100100" to "C", "100110" to "D",
        "100010" to "E", "110100" to "F", "110110" to "G", "110010" to "H",
        "010100" to "I", "010110" to "J", "101000" to "K", "111000" to "L",
        "101100" to "M", "101110" to "N", "101010" to "O", "111100" to "P",
        "111110" to "Q", "111010" to "R", "011100" to "S", "011110" to "T",
        "101001" to "U", "111001" to "V", "010111" to "W", "101101" to "X",
        "101111" to "Y", "101011" to "Z"
    )

    // Maps 6-dot Braille patterns to numbers 0-9
    val numberMap = mapOf(
        "100000" to "1", "110000" to "2", "100100" to "3", "100110" to "4",
        "100010" to "5", "110100" to "6", "110110" to "7", "110010" to "8",
        "010100" to "9", "010110" to "0"
    )

    // Maps 6-dot Braille patterns to symbols/punctuation
    val symbolMap = mapOf(
        "001111" to "#", "010000" to ",", "010011" to ".", "011001" to "?",
        "001000" to "'", "001001" to "-", "011010" to "!"
    )
}
