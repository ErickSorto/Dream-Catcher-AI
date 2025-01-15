package org.ballistic.dreamjournalai.shared.core.domain

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresPermission

actual interface VibratorUtil {
    @RequiresPermission(android.Manifest.permission.VIBRATE)
    actual fun triggerVibration()
    actual fun cancelVibration()
}

class VibratorUtilImpl(
    private val context: Context
) : VibratorUtil {

    @RequiresPermission(android.Manifest.permission.VIBRATE)
    override fun triggerVibration() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(100L)
        }
    }

    override fun cancelVibration() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        vibrator.cancel()
    }
}