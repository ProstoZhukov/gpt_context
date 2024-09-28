package ru.tensor.sbis.richtext.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import ru.tensor.sbis.common.util.ClipboardManager

/**
 * Набор утилит для работы с функциями Android системы.
 *
 * @author am.boldinov
 */
object RichTextAndroidUtil {

    /**
     * Копирует текст в буфер обмена.
     */
    @JvmStatic
    fun copyToClipboard(context: Context, text: String?) {
        if (text.isNullOrEmpty()) {
            return
        }
        ClipboardManager.copyToClipboard(context.applicationContext, text)
        launchHapticVibration(context)
    }

    /**
     * Запускает кратковременную вибрацию.
     * Использовать для пользовательских касаний (тачей).
     */
    @JvmStatic
    fun launchHapticVibration(context: Context) {
        context.getVibrator()?.let { vibrator ->
            val durationPattern = longArrayOf(0, 2)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(durationPattern, -1))
            } else {
                vibrator.vibrate(durationPattern, -1)
            }
        }
    }

    private fun Context.getVibrator(): Vibrator? {
        val vibrator= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
        return vibrator?.takeIf { it.hasVibrator() }
    }
}