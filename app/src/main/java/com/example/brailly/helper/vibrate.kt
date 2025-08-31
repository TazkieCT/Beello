package com.example.brailly.helper

import android.os.VibrationEffect
import android.os.Vibrator
import android.content.Context

/**
 * Extension function for Context to trigger device vibration.
 *
 * @param duration Duration of the vibration in milliseconds. Default is 50ms.
 *
 * Supports both modern (Oreo and above) and legacy vibration APIs.
 */
fun Context.vibrate(duration: Long = 50) {
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        // Use the newer vibration API for devices running Android O or above
        vibrator.vibrate(
            VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
        )
    } else {
        // Fallback for older devices
        @Suppress("DEPRECATION")
        vibrator.vibrate(duration)
    }
}
