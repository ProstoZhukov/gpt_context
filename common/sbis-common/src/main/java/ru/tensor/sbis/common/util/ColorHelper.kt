package ru.tensor.sbis.common.util

import android.graphics.Color
import androidx.annotation.ColorInt
import timber.log.Timber


/**
 * Класс для безопасного парсинга цвета. В случае ошибки вернет черный цвет
 */
object ColorHelper {

    @ColorInt
    fun parse(colorString: String): Int =
            try {
                Color.parseColor(colorString)
            } catch (exception: IllegalArgumentException) {
                getDefaultColor(exception, colorString)
            } catch (exception: StringIndexOutOfBoundsException) {
                getDefaultColor(exception, colorString)
            }

    private fun getDefaultColor(exception: Throwable, colorString: String): Int {
        Timber.w(exception, "Failed parse color $colorString")
        return Color.BLACK
    }


}