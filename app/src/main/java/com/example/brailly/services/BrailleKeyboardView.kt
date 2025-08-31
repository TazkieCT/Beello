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
import com.example.brailly.helper.vibrate

class BrailleKeyboardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val dotRegions = mutableListOf<RectF>()
    private val backgroundPaint = Paint().apply {
        color = Color.argb(180, 0, 0, 0)
        style = Paint.Style.FILL
    }

    init {
        if (context is Activity) {
            context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        dotRegions.clear()

        val columnWidth = w / 2f
        val rowHeight = h / 3f

        for (row in 0..2) {
            val left = 0f
            val top = row * rowHeight
            dotRegions.add(RectF(left, top, columnWidth, top + rowHeight))
        }

        for (row in 0..2) {
            val left = columnWidth
            val top = row * rowHeight
            dotRegions.add(RectF(left, top, columnWidth * 2, top + rowHeight))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        val normalColor = Color.argb(180, 60, 60, 60)
        val pressedColor = Color.argb(220, 30, 30, 30)

        dotRegions.forEachIndexed { index, rect ->
            val paint = Paint().apply {
                color = if (activeDots.contains(index + 1)) pressedColor else normalColor
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect(rect, 20f, 20f, paint)
        }
    }

    private val activeDots = mutableSetOf<Int>()

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                activeDots.clear()
                for (i in 0 until event.pointerCount) {
                    val x = event.getX(i)
                    val y = event.getY(i)
                    dotRegions.forEachIndexed { dotIndex, rect ->
                        if (rect.contains(x, y)) {
                            activeDots.add(dotIndex + 1)
                        }
                    }
                }
                invalidate()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                if (activeDots.isNotEmpty()) {
                    context.vibrate(30)
                    handleBrailleInput(activeDots.toList())
                }
                activeDots.clear()
                invalidate()
            }

            MotionEvent.ACTION_CANCEL -> {
                activeDots.clear()
                invalidate()
            }
        }
        return true
    }

    private fun commitText(text: String) {
        (context as? InputMethodService)?.currentInputConnection?.commitText(text, 1)
    }

    private fun handleBrailleInput(dots: List<Int>) {
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
    }
}
