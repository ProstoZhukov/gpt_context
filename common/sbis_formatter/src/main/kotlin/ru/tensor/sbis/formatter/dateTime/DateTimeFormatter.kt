package ru.tensor.sbis.formatter.dateTime

import android.os.Build
import timber.log.Timber
import java.text.Format
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Форматтер дат с учетом региона.
 *
 * @property region регион пользователя.
 *
 * @author ps.smirnyh
 */
class DateTimeFormatter internal constructor(private val region: String) {

    /**
     * Получить форматтер даты с режимом отображения [mode].
     *
     * @param mode Формат вывода локализованной даты и времени.
     */
    fun getFormatter(mode: DateTimeTranslationMode): Format? {
        val format = DateTimeFormats.formats[region]?.get(mode.name) ?: kotlin.run {
            Timber.e("Unexpected region $region")
            null
        }
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                android.icu.text.SimpleDateFormat(format, Locale.getDefault())
            } else {
                SimpleDateFormat(format, Locale.getDefault())
            }
        } catch (e: IllegalArgumentException) {
            Timber.e(e)
            null
        }
    }
}