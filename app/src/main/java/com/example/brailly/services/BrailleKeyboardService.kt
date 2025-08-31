package com.example.brailly.services

import android.inputmethodservice.InputMethodService
import android.view.View

/**
 * Custom InputMethodService for Braille keyboard input.
 *
 * This service provides a custom keyboard view that allows users
 * to input characters using Braille patterns.
 */
class BrailleKeyboardService : InputMethodService() {

    /**
     * Called to create the input view for the Braille keyboard.
     *
     * @return The custom BrailleKeyboardView used as the keyboard input.
     */
    override fun onCreateInputView(): View {
        return BrailleKeyboardView(this)
    }
}
