package com.example.brailly.models


/**
 * Represents an animal used in the quiz.
 *
 * @property answer Correct answer for the animal.
 * @property soundRes Resource ID of the animal's sound.
 * @property hint Optional hint for the animal.
 */
data class Animal(
    val answer: String,
    val soundRes: Int,
    val hint: String
)