package com.example.brailly.services

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.inputmethodservice.InputMethodService
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.app.Activity
import android.speech.tts.TextToSpeech
import com.example.brailly.helper.vibrate
import java.util.Locale

/**
 * Custom View for Braille keyboard input.
 *
 * Supports six-dot Braille input, real-time visual feedback, and TTS feedback in Indonesian.
 */
class BrailleKeyboardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    // Regions for each Braille dot
    private val dotRegions = mutableListOf<RectF>()

    // Background paint for the keyboard
    private val backgroundPaint = Paint().apply {
        color = Color.argb(180, 0, 0, 0)
        style = Paint.Style.FILL
    }

    private var tts: TextToSpeech? = null

    // Mapping from dot index to logical button number
    private val indexToButtonNumber = mapOf(
        0 to 4,
        1 to 5,
        2 to 6,
        3 to 1,
        4 to 2,
        5 to 3
    )

    init {
        // Initialize TTS for English
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }


        // Force landscape orientation
        if (context is Activity) {
            context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    /** Speak the given text using TTS */
    private fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        dotRegions.clear()

        val columnWidth = w / 2f
        val rowHeight = h / 3f

        // Define left column dots
        for (row in 0..2) {
            val left = 0f
            val top = row * rowHeight
            dotRegions.add(RectF(left, top, columnWidth, top + rowHeight))
        }

        // Define right column dots
        for (row in 0..2) {
            val left = columnWidth
            val top = row * rowHeight
            dotRegions.add(RectF(left, top, columnWidth * 2, top + rowHeight))
        }
    }

    private val activeDots = mutableSetOf<Int>()
    private val pressedDots = mutableSetOf<Int>()

    private var currentChar: String? = null
    private var charAlpha = 255
    private var fading = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        val normalColor = Color.argb(180, 60, 60, 60)
        val activeColor = Color.argb(220, 30, 30, 30)

        // Draw each Braille dot with proper visual feedback
        dotRegions.forEachIndexed { index, rect ->
            val paint = Paint().apply {
                color = if (activeDots.contains(indexToButtonNumber[index])
                    || pressedDots.contains(indexToButtonNumber[index]))
                    activeColor else normalColor
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect(rect, 20f, 20f, paint)
        }

        // Draw the current character in the middle
        currentChar?.let {
            val paint = Paint().apply {
                color = Color.WHITE
                textSize = 200f
                textAlign = Paint.Align.CENTER
                alpha = charAlpha
            }
            val x = width / 2f
            val y = height / 2f - (paint.descent() + paint.ascent()) / 2
            canvas.drawText(it, x, y, paint)
        }

        // Handle fading effect
        if (fading && charAlpha > 0) {
            charAlpha -= 10
            if (charAlpha < 0) charAlpha = 0
            postInvalidateOnAnimation()
        }
    }

    /** Display a character temporarily */
    private fun showChar(char: String) {
        currentChar = char
        charAlpha = 255
        fading = true
        invalidate()

        postDelayed({
            charAlpha = 0
            invalidate()
            currentChar = null
        }, 500)
    }

    private var pendingDots = mutableSetOf<Int>()
    private var inputDelayRunnable: Runnable? = null
    private val inputDelay = 300L

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                // Hapus timer delay sebelumnya (karena user lanjut ngetik)
                removeCallbacks(inputDelayRunnable)

                // Deteksi dot yang disentuh
                val touchedDots = mutableSetOf<Int>()
                for (i in 0 until event.pointerCount) {
                    val x = event.getX(i)
                    val y = event.getY(i)
                    dotRegions.forEachIndexed { dotIndex, rect ->
                        if (rect.contains(x, y)) {
                            indexToButtonNumber[dotIndex]?.let { btn ->
                                touchedDots.add(btn)
                            }
                        }
                    }
                }

                // Tambahkan ke kombinasi aktif
                activeDots.addAll(touchedDots)
                pendingDots.addAll(touchedDots)
                invalidate()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                // Update dots terakhir
                pressedDots.clear()
                pressedDots.addAll(pendingDots)
                invalidate()

                // Reset timer lama dan buat ulang
                removeCallbacks(inputDelayRunnable)
                inputDelayRunnable = Runnable {
                    if (pendingDots.isNotEmpty()) {
                        context.vibrate(30)
                        handleBrailleInput(pendingDots.toList())
                        activeDots.clear()
                        pressedDots.clear()
                        pendingDots.clear()
                        invalidate()
                    }
                }
                postDelayed(inputDelayRunnable, inputDelay)
            }

            MotionEvent.ACTION_CANCEL -> {
                activeDots.clear()
                pendingDots.clear()
                pressedDots.clear()
                invalidate()
            }
        }
        return true
    }

    /** Commit text to the input connection */
    private fun commitText(text: String) {
        (context as? InputMethodService)?.currentInputConnection?.commitText(text, 1)
    }

    /** Handle Braille input and map dots to characters or actions */
    private fun handleBrailleInput(dots: List<Int>) {
        val inputConnection = (context as? InputMethodService)?.currentInputConnection

        // Single-dot commands
        when {
            dots.contains(2) && dots.size == 1 -> {
                commitText(" ")
                speak("space")
                return
            }
            dots.contains(3) && dots.size == 1 -> {
                inputConnection?.deleteSurroundingText(1, 0)
                speak("delete")
                return
            }
            dots.contains(4) && dots.size == 1 -> {
                val extracted = inputConnection?.getExtractedText(
                    android.view.inputmethod.ExtractedTextRequest(), 0
                )?.text?.toString()
                if (!extracted.isNullOrEmpty()) speak(extracted) else speak("no text")
                return
            }
            dots.contains(5) && dots.size == 1 -> {
                commitText("\n")
                speak("enter")
                return
            }
            dots.contains(6) && dots.size == 1 -> {
                val extracted = inputConnection?.getExtractedText(
                    android.view.inputmethod.ExtractedTextRequest(), 0
                )?.text?.toString()
                if (!extracted.isNullOrEmpty()) {
                    val lastSpace = extracted.lastIndexOf(' ')
                    val deleteCount =
                        if (lastSpace == -1) extracted.length else extracted.length - lastSpace
                    inputConnection.deleteSurroundingText(deleteCount, 0)
                }
                speak("delete word")
                return
            }
        }

        // Map Braille dots to letters
        val brailleMap = mapOf(
            listOf(1) to "A",
            listOf(1, 2) to "B",
            listOf(1, 4) to "C",
            listOf(1, 4, 5) to "D",
            listOf(1, 5) to "E",
            listOf(1, 2, 4) to "F",
            listOf(1, 2, 4, 5) to "G",
            listOf(1, 2, 5) to "H",
            listOf(2, 4) to "I",
            listOf(2, 4, 5) to "J",
            listOf(1, 3) to "K",
            listOf(1, 2, 3) to "L",
            listOf(1, 3, 4) to "M",
            listOf(1, 3, 4, 5) to "N",
            listOf(1, 3, 5) to "O",
            listOf(1, 2, 3, 4) to "P",
            listOf(1, 2, 3, 4, 5) to "Q",
            listOf(1, 2, 3, 5) to "R",
            listOf(2, 3, 4) to "S",
            listOf(2, 3, 4, 5) to "T",
            listOf(1, 3, 6) to "U",
            listOf(1, 2, 3, 6) to "V",
            listOf(2, 4, 5, 6) to "W",
            listOf(1, 3, 4, 6) to "X",
            listOf(1, 3, 4, 5, 6) to "Y",
            listOf(1, 3, 5, 6) to "Z"
        )

        val char = brailleMap[dots.sorted()]
        commitText(char ?: "")
        if (!char.isNullOrEmpty()) {
            speak(char)
            showChar(char)
        } else {
            speak("not found")
        }

    }
}
