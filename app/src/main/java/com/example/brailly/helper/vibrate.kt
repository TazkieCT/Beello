package com.example.brailly.helper

import android.os.VibrationEffect
import android.os.Vibrator
import android.content.Context

fun Context.vibrate(duration: Long = 50) {
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        vibrator.vibrate(
            VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
        )
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(duration)
    }
}