package org.ballistic.dreamjournalai.core.util

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator


object VibrationUtil {
    // Static method to trigger vibration
    fun triggerVibration(vibrator: Vibrator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android Q and above
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        }
    }
}