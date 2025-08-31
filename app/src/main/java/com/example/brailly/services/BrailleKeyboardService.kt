package com.example.brailly.services

import android.inputmethodservice.InputMethodService
import android.view.View

class BrailleKeyboardService : InputMethodService() {
    override fun onCreateInputView(): View {
        return BrailleKeyboardView(this)
    }
}
